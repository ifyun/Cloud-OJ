package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.dao.*;
import cloud.oj.judge.entity.Limit;
import cloud.oj.judge.entity.RunResult;
import cloud.oj.judge.entity.Runtime;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.error.UnsupportedLanguageError;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.StringJoiner;

@Slf4j
@Component
public class Judgement {

    private final AppConfig appConfig;

    private final ObjectMapper objectMapper;

    private final RuntimeDao runtimeDao;

    private final CompileDao compileDao;

    private final ProblemDao problemDao;

    private final SolutionDao solutionDao;

    private final RankingDao rankingDao;

    private final DatabaseConfig dbConfig;

    private final Compiler compiler;

    private final HashMap<String, Integer> cpuMap;

    private static final int RE = 1;
    private static final int IE = 2;

    private final static UnixDomainSocketAddress addr = UnixDomainSocketAddress.of("/var/run/judge.sock");

    @Autowired
    public Judgement(AppConfig appConfig, ObjectMapper objectMapper, RuntimeDao runtimeDao, CompileDao compileDao,
                     ProblemDao problemDao, SolutionDao solutionDao, RankingDao rankingDao, DatabaseConfig dbConfig,
                     Compiler compiler, HashMap<String, Integer> cpuMap) {
        this.appConfig = appConfig;
        this.objectMapper = objectMapper;
        this.runtimeDao = runtimeDao;
        this.compileDao = compileDao;
        this.problemDao = problemDao;
        this.solutionDao = solutionDao;
        this.rankingDao = rankingDao;
        this.dbConfig = dbConfig;
        this.compiler = compiler;
        this.cpuMap = cpuMap;
    }

    private static class InternalError extends Exception {
        InternalError(String msg) {
            super(msg);
        }
    }

    /**
     * 判题入口
     * <p>隔离级别：读提交</p>
     *
     * @param solution {@link Solution}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void judge(Solution solution) {
        log.debug("Judging: solution({}), user({}).", solution.getSolutionId(), solution.getUserId());
        // 为当前事务禁用外键约束
        dbConfig.disableFKChecks();

        var compile = compiler.compile(solution);
        compileDao.add(compile);

        if (compile.getState() == 0) {
            var limit = problemDao.getLimit(solution.getProblemId());
            var runtime = new Runtime(solution.getSolutionId());
            runtimeDao.add(runtime);

            var result = execute(solution, runtime, limit);
            saveResult(solution, runtime, result, limit);
            runtimeDao.update(runtime);
        } else {
            solution.setResult(SolutionResult.CE);
            solution.setState(SolutionState.JUDGED);
            solutionDao.updateState(solution);
        }
    }

    /**
     * 计算并结果
     * <p>计算分数并更新排名</p>
     *
     * @param result {@link RunResult}
     */
    private void saveResult(Solution solution, Runtime runtime, RunResult result, Limit limit) {
        if (runtime.getResult().equals(SolutionResult.IE) || runtime.getResult().equals(SolutionResult.RE)) {
            solution.setResult(runtime.getResult());
            solution.setState(SolutionState.JUDGED);
            solutionDao.updateState(solution);
            return;
        }

        var userId = solution.getUserId();
        var problemId = solution.getProblemId();
        var contestId = solution.getContestId();

        var passRate = result.getPassRate();

        if (Double.isNaN(passRate)) {
            passRate = 0d;
        }

        // 查询历史提交中的最高分
        var maxScore = solutionDao.getMaxScoreOfUser(userId, problemId, contestId);

        runtime.setTotal(result.getTotal());
        runtime.setPassed(result.getPassed());
        runtime.setTime(result.getTime());
        runtime.setMemory(result.getMemory());

        solution.setResult(SolutionResult.fromString(result.getResult()));
        solution.setPassRate(passRate);
        solution.setScore(passRate * limit.getScore());
        solution.setState(SolutionState.JUDGED);

        solutionDao.updateState(solution);

        // 本次得分不为 0 且历史最高分小于本次得分时才更新排名
        if (passRate > 0 && (maxScore == null || maxScore < solution.getScore())) {
            if (contestId == null) {
                rankingDao.update(userId, solution.getSubmitTime());
            } else {
                rankingDao.updateForContest(contestId, userId, solution.getSubmitTime());
            }
        } else {
            // 仅更新提交次数
            if (contestId == null) {
                rankingDao.incCommitted(userId);
            } else {
                rankingDao.incCommittedForContest(contestId, userId);
            }
        }
    }

    /**
     * 执行用户程序
     *
     * @return 运行结果 {@link RunResult}
     */
    private RunResult execute(Solution solution, Runtime runtime, Limit limit) {
        RunResult result = null;

        try {
            var testDataDir = appConfig.getFileDir() + "data/" + solution.getProblemId();
            var argv = buildCommand(solution, limit, testDataDir);
            var buf = ByteBuffer.allocate(2048);
            var channel = SocketChannel.open(StandardProtocolFamily.UNIX);
            channel.connect(addr);
            channel.write(ByteBuffer.wrap(argv.getBytes()));
            channel.read(buf);
            channel.close();
            buf.flip();
            var bytes = new byte[buf.remaining()];
            buf.get(bytes);
            result = objectMapper.readValue(bytes, RunResult.class);

            if (result.getCode() == RE) {
                log.info("运行错误({}): {}", solution.getSolutionId(), result.getError());
                runtime.setInfo(result.getError());
                runtime.setResult(SolutionResult.RE);
            } else if (result.getCode() == IE) {
                log.info("内部错误({}): {}", solution.getSolutionId(), result.getError());
                throw new InternalError(result.getError());
            } else {
                runtime.setResult(0);
            }
        } catch (IOException | InternalError | UnsupportedLanguageError e) {
            log.info("内部错误({}): {}", solution.getSolutionId(), e.getMessage());
            runtime.setResult(SolutionResult.IE);
            runtime.setInfo(e.getMessage());
        }

        runtimeDao.update(runtime);
        return result;
    }

    /**
     * 生成命令
     */
    private String buildCommand(Solution solution, Limit limit, String dataDir)
            throws UnsupportedLanguageError {
        var language = Language.get(solution.getLanguage());

        if (language == null) {
            throw new UnsupportedLanguageError("NULL");
        }

        var cpu = cpuMap.get(Thread.currentThread().getName());

        if (cpu == null) {
            // 在提交线程时，cpu 为 null
            cpu = appConfig.getCpus().get(0);
        }

        var workDir = appConfig.getCodeDir() + solution.getSolutionId();
        var timeLimit = limit.getTimeout();
        var memoryLimit = limit.getMemoryLimit();
        var outputLimit = limit.getOutputLimit();

        StringJoiner argv = new StringJoiner(" ");

        switch (language) {
            case C, CPP, GO -> argv.add("--cmd=./Solution");
            case JAVA -> {
                memoryLimit <<= 1;
                argv.add("--cmd=java@@Solution");
            }
            case KOTLIN -> {
                timeLimit <<= 1;
                memoryLimit <<= 1;
                argv.add("--cmd=kotlin@SolutionKt");
            }
            case JAVA_SCRIPT -> {
                memoryLimit <<= 1;
                argv.add("--cmd=node@Solution.js");
            }
            case PYTHON -> argv.add("--cmd=python3@Solution.py");
            case BASH -> argv.add("--cmd=sh@Solution.sh");
            case C_SHARP -> {
                memoryLimit <<= 1;
                argv.add("--cmd=mono@Solution.exe");
            }
            default -> throw new UnsupportedLanguageError(language.toString());
        }

        return argv
                .add("--time=" + timeLimit)
                .add("--ram=" + memoryLimit)
                .add("--output=" + outputLimit)
                .add("--workdir=" + workDir)
                .add("--data=" + dataDir)
                .add("--lang=" + solution.getLanguage())
                .add("--cpu=" + (cpu == null ? 0 : cpu))
                .toString();
    }
}

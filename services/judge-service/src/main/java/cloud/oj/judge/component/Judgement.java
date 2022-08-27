package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.dao.*;
import cloud.oj.judge.error.UnsupportedLanguageError;
import cloud.oj.judge.model.Limit;
import cloud.oj.judge.model.RunResult;
import cloud.oj.judge.model.Runtime;
import cloud.oj.judge.model.Solution;
import cloud.oj.judge.type.Language;
import cloud.oj.judge.type.SolutionResult;
import cloud.oj.judge.type.SolutionState;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class Judgement {

    @Resource
    private AppConfig appConfig;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RuntimeDao runtimeDao;

    @Resource
    private CompileDao compileDao;

    @Resource
    private ProblemDao problemDao;

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private RankingDao rankingDao;

    @Resource
    private DatabaseConfig dbConfig;

    @Resource
    private Compiler compiler;

    @Resource
    private HashMap<String, Integer> cpus;

    private static class RuntimeError extends Exception {
        RuntimeError(String msg) {
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
        log.info("Judging: solution({}), user({}).", solution.getSolutionId(), solution.getUserId());

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
            solutionDao.update(solution);
        }
    }

    /**
     * 计算并结果
     * <p>计算分数并更新排名</p>
     *
     * @param result {@link RunResult}
     */
    private void saveResult(Solution solution, Runtime runtime, RunResult result, Limit limit) {
        if (runtime.getResult() == SolutionResult.IE || runtime.getResult() == SolutionResult.RE) {
            solution.setResult(runtime.getResult());
            solution.setState(SolutionState.JUDGED);
            solutionDao.update(solution);
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

        solution.setResult(SolutionResult.getByString(result.getResult()));
        solution.setPassRate(passRate);
        solution.setScore(passRate * limit.getScore());
        solution.setState(SolutionState.JUDGED);

        solutionDao.update(solution);

        // 本次得分不为 0 且历史最高分小于本次得分时才更新排名
        if (passRate > 0 && (maxScore == null || maxScore < solution.getScore())) {
            if (contestId == null) {
                rankingDao.update(userId, solution.getSubmitTime());
            } else {
                rankingDao.updateContest(contestId, userId, solution.getSubmitTime());
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
            String testDataDir = appConfig.getFileDir() + "test_data/" + solution.getProblemId();
            ProcessBuilder cmd = buildCommand(solution, limit, testDataDir);
            result = run(cmd);
        } catch (RuntimeError e) {
            log.warn("Runtime Error: {}", e.getMessage());
            runtime.setInfo(e.getMessage());
            runtime.setResult(SolutionResult.RE);
        } catch (InterruptedException | IOException | UnsupportedLanguageError e) {
            log.warn("Judge Error: {}", e.getMessage());
            runtime.setResult(SolutionResult.IE);
            runtime.setInfo(e.getMessage());
        }

        runtimeDao.update(runtime);
        return result;
    }

    /**
     * 调用判题程序执行
     *
     * @param cmd 命令 & 参数
     * @return 运行结果 {@link RunResult}
     */
    private RunResult run(ProcessBuilder cmd)
            throws RuntimeError, IOException, InterruptedException {
        RunResult result;
        var process = cmd.start();

        int exitValue = process.waitFor();

        if (exitValue == 0) {
            // 正常退出
            var resultStr = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            result = objectMapper.readValue(resultStr, RunResult.class);
        } else {
            // 非正常退出
            var stderr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            if (exitValue == 1) {
                throw new RuntimeError(stderr);
            } else {
                throw new InterruptedException(stderr);
            }
        }

        process.destroy();
        return result;
    }

    /**
     * 生成命令
     */
    private ProcessBuilder buildCommand(Solution solution, Limit limit, String testDataDir)
            throws UnsupportedLanguageError {
        var language = Language.get(solution.getLanguage());

        if (language == null) {
            throw new UnsupportedLanguageError("NULL");
        }

        var cpu = cpus.get(Thread.currentThread().getName());   // 获取与当前线程绑定的 CPU ID

        var solutionDir = appConfig.getCodeDir() + solution.getSolutionId();
        var builder = new ProcessBuilder();
        List<String> cmd = new ArrayList<>();

        cmd.add("/opt/bin/judge-runner");

        var timeLimit = limit.getTimeout();
        var outputLimit = limit.getOutputLimit();
        var memoryLimit = limit.getMemoryLimit();

        switch (language) {
            case C:
            case CPP:
            case GO:
                cmd.add("--cmd=./Solution");
                break;
            case JAVA:
                memoryLimit <<= 1;
                cmd.add("--cmd=java@Solution");
                break;
            case KOTLIN:
                timeLimit <<= 1;
                memoryLimit <<= 1;
                cmd.add("--cmd=kotlin@SolutionKt");
                break;
            case JAVA_SCRIPT:
                memoryLimit <<= 1;
                cmd.add("--cmd=node@Solution.js");
                break;
            case PYTHON:
                cmd.add("--cmd=python3@Solution.py");
                break;
            case BASH:
                cmd.add("--cmd=sh@Solution.sh");
                break;
            case C_SHARP:
                memoryLimit <<= 1;
                cmd.add("--cmd=mono@Solution.exe");
                break;
            default:
                throw new UnsupportedLanguageError(language.toString());
        }

        var config = Arrays.asList(
                "--time=" + timeLimit,
                "--memory=" + memoryLimit,
                "--output-size=" + outputLimit,
                "--workdir=" + solutionDir,
                "--data=" + testDataDir,
                "--lang=" + solution.getLanguage(),
                "--cpu=" + (cpu == null ? 0 : cpu)
        );

        cmd.addAll(config);
        builder.command(cmd);
        return builder;
    }
}

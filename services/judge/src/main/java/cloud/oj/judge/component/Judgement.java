package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.constant.State;
import cloud.oj.judge.dao.ProblemDao;
import cloud.oj.judge.dao.RankingDao;
import cloud.oj.judge.dao.SolutionDao;
import cloud.oj.judge.entity.Problem;
import cloud.oj.judge.entity.Result;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.error.UnsupportedLanguageError;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static cloud.oj.judge.constant.Language.*;
import static cloud.oj.judge.entity.Result.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class Judgement {

    private final AppConfig appConfig;

    private final ProblemDao problemDao;

    private final SolutionDao solutionDao;

    private final RankingDao rankingDao;

    private final Compiler compiler;

    private final HashMap<String, Integer> cpuMap;

    private final ObjectMapper objectMapper;

    private final ProcessBuilder processBuilder = new ProcessBuilder();

    /**
     * 判题入口
     * <p>隔离级别：读提交</p>
     *
     * @param solution {@link Solution}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void judge(Solution solution) {
        var compile = compiler.compile(solution);

        if (compile.getState() == 0) {
            // 编译成功
            var problem = problemDao.getById(solution.getProblemId());
            // 更新为正在运行状态
            solutionDao.updateState(solution.getSolutionId(), State.RUNNING);
            // 运行
            var result = execute(solution, problem);
            saveResult(solution, result, problem);
        } else {
            // 编译失败
            solution.endWithError(CE, compile.getInfo());
            solutionDao.updateWithResult(solution);
        }
    }

    /**
     * 保存判题结果
     * <p>计算分数并更新排名</p>
     *
     * @param result {@link Result}
     */
    private void saveResult(Solution solution, Result result, Problem problem) {
        // 内部错误
        if (result.getResult().equals(IE)) {
            solution.endWithError(result.getResult(), result.getError());
            solutionDao.updateWithResult(solution);
            return;
        }

        var err = result.getError();
        // 运行时错误
        if (err != null && err.isEmpty()) {
            solution.setErrorInfo(result.getError());
        }

        var uid = solution.getUid();
        var problemId = solution.getProblemId();
        var contestId = solution.getContestId();

        var passRate = result.getPassRate();

        if (Double.isNaN(passRate)) {
            passRate = 0d;
        }

        // 查询历史提交中的最高分
        var maxScore = solutionDao.getMaxScoreOfUser(uid, problemId, contestId);

        solution.setTotal(result.getTotal());
        solution.setPassed(result.getPassed());
        solution.setPassRate(passRate);
        solution.setScore(passRate * problem.getScore());
        solution.setTime(result.getTime());
        solution.setMemory(result.getMemory());
        solution.setResult(result.getResult());
        solution.setState(State.JUDGED);

        solutionDao.updateWithResult(solution);

        // 更新排名
        // 本次得分不为 0 且历史最高分小于本次得分时才更新排名
        if (passRate > 0 && (maxScore == null || maxScore < solution.getScore())) {
            if (contestId == null) {
                rankingDao.update(uid, solution.getSubmitTime());
            } else {
                rankingDao.updateForContest(contestId, uid, solution.getSubmitTime());
            }
        } else {
            // 仅更新提交次数
            if (contestId == null) {
                rankingDao.incCommitted(uid, solution.getSubmitTime());
            } else {
                rankingDao.incCommittedForContest(uid, contestId, solution.getSubmitTime());
            }
        }
    }

    /**
     * 运行用户程序
     *
     * @return 运行结果 {@link Result}
     */
    private Result execute(Solution solution, Problem problem) {
        Process process = null;
        Result result;

        try {
            check(solution.getLanguage());

            var bin = "judge";
            var lang = "--lang=" + solution.getLanguage();
            var time = "--time=" + problem.getTimeout();
            var ram = "--ram=" + problem.getMemoryLimit();
            var output = "--output=" + problem.getOutputLimit();
            var cpu = "--cpu=" + cpuMap.get(Thread.currentThread().getName());
            var workdir = "--workdir=" + appConfig.getCodeDir() + solution.getSolutionId();
            var data = "--data=" + appConfig.getFileDir() + "data/" + solution.getProblemId();
            var cmd = switch (solution.getLanguage()) {
                case C, CPP, GO -> "--cmd=./Solution";
                case JAVA -> "--cmd=java@-Xmx256m@Solution";
                case KOTLIN -> "--cmd=./Solution.kexe";
                case JAVA_SCRIPT -> "--cmd=node@Solution.js";
                case PYTHON -> "--cmd=python3@Solution.py";
                case BASH -> "--cmd=sh@Solution.sh";
                case C_SHARP -> "--cmd=mono@Solution.exe";
                default -> "";
            };

            process = processBuilder.command(bin, cmd, lang, time, ram, cpu, output, workdir, data).start();
            process.waitFor();

            if (process.exitValue() != 0) {
                // 非零退出
                log.error(IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8));
                result = withError(IE, "JUDGE NON-ZERO EXIT");
            } else {
                result = objectMapper.readValue(process.getInputStream(), Result.class);
            }
        } catch (UnsupportedLanguageError e) {
            result = withError(IE, e.getMessage());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            result = withError(IE, "JUDGE THREAD ERROR");
        } catch (IOException e) {
            log.error(e.getMessage());
            result = withError(IE, "JUDGE THREAD IO ERROR");
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return result;
    }
}

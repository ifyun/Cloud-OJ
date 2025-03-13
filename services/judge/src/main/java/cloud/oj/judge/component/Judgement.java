package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.constant.State;
import cloud.oj.judge.entity.Problem;
import cloud.oj.judge.entity.Result;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.error.UnsupportedLanguageError;
import cloud.oj.judge.repo.ProblemRepo;
import cloud.oj.judge.repo.ScoreboardRepo;
import cloud.oj.judge.repo.SolutionRepo;
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static cloud.oj.judge.constant.Language.*;
import static cloud.oj.judge.entity.Result.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class Judgement {

    private final AppConfig appConfig;

    private final ProblemRepo problemRepo;

    private final SolutionRepo solutionRepo;

    private final ScoreboardRepo scoreboardRepo;

    private final Compiler compiler;

    private final HashMap<String, Integer> cpuMap;

    private final ObjectMapper objectMapper;

    private final ProcessBuilder processBuilder = new ProcessBuilder();

    /**
     * 判题入口
     *
     * @param solution {@link Solution}
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void judge(Solution solution) {
        var dataConf = problemRepo.selectDataConf(solution.getProblemId());
        var compile = compiler.compile(solution);

        if (compile.getState() == 0) {
            // 编译成功
            var problem = problemRepo.selectById(solution.getProblemId());
            // 更新为正在运行状态
            solutionRepo.updateState(solution.getId(), State.RUNNING);
            // 运行
            var result = execute(solution, problem);
            // 保存结果
            saveResult(solution, result, problem, dataConf);
        } else {
            // 编译失败
            solution.endWithError(CE, compile.getInfo());
            solutionRepo.updateResult(solution);
        }
    }

    /**
     * 保存判题结果，计算分数并更新排名
     *
     * @param result {@link Result}
     */
    private void saveResult(Solution solution, Result result, Problem problem,
                            Optional<Map<String, Integer>> dataConf) {
        // 内部错误，结束
        if (result.getResult().equals(IE)) {
            solution.endWithError(result.getResult(), result.getError());
            solutionRepo.updateResult(solution);
            return;
        }

        var err = result.getError();
        // 运行时错误，仍然统计分数
        if (err != null && !err.isEmpty()) {
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
        var maxScore = solutionRepo.selectMaxScoreOfUser(uid, problemId, contestId);
        var score = 0.0;

        if (dataConf.isPresent() && result.getDetail() != null && !result.getDetail().isEmpty()) {
            // 根据通过的测试点计算分数
            var data = dataConf.get();
            for (var fileName : result.getDetail()) {
                score += data.get(fileName);
            }
        } else {
            // 兼容没有指定测试数据分数的旧题目
            score = passRate * problem.getScore();
        }

        solution.setTotal(result.getTotal());
        solution.setPassed(result.getPassed());
        solution.setPassRate(passRate);
        solution.setScore(score);
        solution.setTime(result.getTime());
        solution.setMemory(result.getMemory());
        solution.setResult(result.getResult());
        solution.setState(State.JUDGED);

        solutionRepo.updateResult(solution);

        // 更新排名
        // 本次得分不为 0 且历史最高分小于本次得分时才更新排名
        if (passRate > 0 && (maxScore == null || maxScore < solution.getScore())) {
            if (contestId == null) {
                scoreboardRepo.update(uid, solution.getSubmitTime());
            } else {
                scoreboardRepo.updateOfContest(contestId, uid, solution.getSubmitTime());
            }
        } else {
            // 仅更新提交次数
            if (contestId == null) {
                scoreboardRepo.incSubmitted(uid, solution.getSubmitTime());
            } else {
                scoreboardRepo.incSubmittedOfContest(uid, contestId, solution.getSubmitTime());
            }
        }
    }

    /**
     * 运行用户程序
     *
     * @return 运行结果 {@link Result}
     */
    private Result execute(Solution solution, Problem problem) {
        Result result;

        try {
            check(solution.getLanguage());

            var bin = "judge";
            var time = "--time=" + switch (solution.getLanguage()) {
                case JAVA, JAVA_SCRIPT -> problem.getTimeout() * 2;
                default -> problem.getTimeout();
            };
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
                case C_SHARP -> "--cmd=dotnet@Solution.exe";
                default -> "";
            };

            var timeout = new AtomicBoolean(false);
            var process = processBuilder.command(bin, cmd, time, ram, cpu, output, workdir, data).start();
            ProcessUtil.watchProcess(25, process, timeout);
            process.waitFor();

            if (timeout.get()) {
                throw new InterruptedException("运行超时");
            }

            if (process.exitValue() != 0) {
                // 非零退出
                log.warn("JUDGE 非零退出(sid={}): {}", solution.getSolutionId(),
                        IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8));
                result = withError(IE, "JUDGE 非零退出");
            } else {
                result = objectMapper.readValue(process.getInputStream(), Result.class);
            }
        } catch (UnsupportedLanguageError e) {
            result = withError(IE, e.getMessage());
        } catch (InterruptedException e) {
            log.error("JUDGE 线程中断(sid={}): {}", solution.getSolutionId(), e.getMessage());
            result = withError(IE, e.getMessage());
        } catch (IOException e) {
            log.error("JUDGE IO ERROR(sid={}): {}", solution.getSolutionId(), e.getMessage());
            result = withError(IE, "JUDGE 线程 IO 错误");
        }

        return result;
    }
}

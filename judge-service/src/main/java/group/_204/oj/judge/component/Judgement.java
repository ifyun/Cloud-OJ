package group._204.oj.judge.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import group._204.oj.judge.dao.RuntimeDao;
import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.model.*;
import group._204.oj.judge.model.Runtime;
import group._204.oj.judge.type.Language;
import group._204.oj.judge.type.SolutionResult;
import group._204.oj.judge.type.SolutionState;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 判题模块
 */
@Slf4j
@Component
public class Judgement {

    @Value("${project.file-dir}")
    private String fileDir;

    @Value("${project.code-dir}")
    private String codeDir;

    @Value("${project.runner-image}")
    private String runnerImage;

    private static final String MAX_MEM_LIMIT = "524287";
    private static final String MEM_LIMIT = "65535";

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RuntimeDao runtimeDao;

    @Resource
    private SolutionDao solutionDao;

    private static class RuntimeError extends Exception {
        RuntimeError(String msg) {
            super(msg);
        }
    }

    private static class TimeoutError extends Exception {
        TimeoutError(String msg) {
            super(msg);
        }
    }

    private static class LanguageNotSupportError extends Exception {
        LanguageNotSupportError(String msg) {
            super(msg);
        }
    }

    private static final int AC = 0, TLE = 2, MLE = 3, RE = 4;

    /**
     * 判题
     *
     * @param solution {@link Solution} 答案对象
     */
    public void judge(Solution solution) {
        long timeout = runtimeDao.getTimeout(solution.getProblemId());

        log.info("正在判题: solutionId={}.", solution.getSolutionId());

        Runtime runtime = new Runtime(solution.getSolutionId());
        runtimeDao.add(runtime);

        List<RunResult> results = execute(solution, runtime, timeout);
        calcResult(solution, runtime, results);
    }

    /**
     * Calculate results
     *
     * @param results List of run result
     */
    private void calcResult(Solution solution, Runtime runtime, List<RunResult> results) {
        if (runtime.getResult() == SolutionResult.JUDGE_ERROR
                || runtime.getResult() == SolutionResult.RUNTIME_ERROR) {
            solution.setResult(runtime.getResult());
        } else {
            Long time = results.stream().max(Comparator.comparingLong(RunResult::getTimeUsed)).get().getTimeUsed();
            Long memory = results.stream().max(Comparator.comparingLong(RunResult::getMemUsed)).get().getMemUsed();

            // Group by status
            Map<Integer, Long> statusMap =
                    results.stream().collect(Collectors.groupingBy(RunResult::getStatus, Collectors.counting()));

            Long ac = statusMap.get(AC),
                    tle = statusMap.get(TLE),
                    mle = statusMap.get(MLE),
                    re = statusMap.get(RE);

            int total = results.size();

            runtime.setTotal(total);
            runtime.setPassed(ac);
            runtime.setTime(time);
            runtime.setMemory(memory);

            double passRate = 0;

            if (ac == null) {
                solution.setResult(SolutionResult.WRONG);
            } else if (ac < total) {
                passRate = (double) ac / total;
                solution.setResult(SolutionResult.PARTLY_PASSED);
            } else {
                passRate = 1;
                solution.setResult(SolutionResult.PASSED);
            }

            if (tle != null)
                solution.setResult(SolutionResult.TIMEOUT);
            if (mle != null)
                solution.setResult(SolutionResult.OOM);
            if (re != null)
                solution.setResult(SolutionResult.RUNTIME_ERROR);

            solution.setPassRate(Double.isNaN(passRate) ? 0 : passRate);
        }

        solution.setState(SolutionState.JUDGED);
        runtimeDao.update(runtime);
        solutionDao.update(solution);
    }

    /**
     * Execute the compiled code
     *
     * @return List of run result
     */
    private List<RunResult> execute(Solution solution, Runtime runtime, long timeout) {
        List<RunResult> results = null;

        try {
            String testDataDir = fileDir + "test_data/" + solution.getProblemId();
            ProcessBuilder cmd = buildCommand(solution, String.valueOf(timeout), testDataDir);
            results = run(cmd, solution.getSolutionId());
        } catch (RuntimeError e) {
            log.warn("Runtime Error: {}", e.getMessage());
            runtime.setInfo(e.getMessage());
            runtime.setResult(SolutionResult.RUNTIME_ERROR);
        } catch (InterruptedException | IOException | TimeoutError | LanguageNotSupportError e) {
            log.error("Judge Error: {}", e.getMessage());
            runtime.setResult(SolutionResult.JUDGE_ERROR);
            runtime.setInfo(e.getMessage());
        }

        runtimeDao.update(runtime);

        return results;
    }

    /**
     * Run command and get result
     *
     * @param cmd Command to run
     * @return {@link RunResult} Contains status and stdout
     */
    private List<RunResult> run(ProcessBuilder cmd, String solutionId) throws RuntimeError, TimeoutError, IOException, InterruptedException {
        Process process = cmd.start();

        if (!process.waitFor(6000, TimeUnit.MILLISECONDS)) {
            throw new TimeoutError("Wait timeout.");
        }

        List<RunResult> results;
        String stderr = getOutput(process.getErrorStream());

        if (stderr.isEmpty()) {
            String solutionDir = codeDir + solutionId;
            // Get run result
            String resultStr = getOutputFromFile(solutionDir + "/result.json");
            results = objectMapper.readValue(resultStr, new TypeReference<List<RunResult>>() {
            });
        } else {
            throw new RuntimeError(stderr);
        }

        return results;
    }

    @SneakyThrows
    private String getOutput(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String output = reader.lines().collect(Collectors.joining("\n"));
        reader.close();
        return output;
    }

    @SneakyThrows
    private String getOutputFromFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        }

        return "";
    }

    /**
     * Build command to run code
     *
     * @param solution {@link Solution} Solution object
     * @return {@link ProcessBuilder} Command to run
     */
    private ProcessBuilder buildCommand(Solution solution, String timeout, String testDataDir) throws LanguageNotSupportError {
        Language language = Language.get(solution.getLanguage());
        assert language != null;

        String solutionDir = codeDir + solution.getSolutionId();

        ProcessBuilder builder = new ProcessBuilder();

        List<String> cmd = new ArrayList<>(Arrays.asList(
                "docker", "run", "--rm", "--network", "none", "-v", solutionDir + ":" + solutionDir,
                "-v", fileDir + ":" + fileDir + ":ro", "-w", solutionDir, runnerImage, "runner"
        ));

        switch (language) {
            case C:
            case CPP:
                cmd.addAll(Arrays.asList("./Solution", timeout, MEM_LIMIT, MEM_LIMIT, testDataDir));
                builder.command(cmd);
                return builder;
            case JAVA:
                cmd.addAll(Arrays.asList("java@Solution", timeout, MEM_LIMIT, MAX_MEM_LIMIT, testDataDir));
                builder.command(cmd);
                return builder;
            case PYTHON:
                cmd.addAll(Arrays.asList("python@Solution.py", timeout, MEM_LIMIT, MEM_LIMIT, testDataDir));
                builder.command(cmd);
                return builder;
            case BASH:
                cmd.addAll(Arrays.asList("sh@Solution.sh", timeout, MEM_LIMIT, MEM_LIMIT, testDataDir));
                builder.command(cmd);
                return builder;
            case C_SHARP:
                cmd.addAll(Arrays.asList("mono@Solution.exe", timeout, MEM_LIMIT, MEM_LIMIT, testDataDir));
                builder.command(cmd);
                return builder;
            default:
                throw new LanguageNotSupportError("Language not support.");
        }
    }
}

package group._204.oj.judge.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import group._204.oj.judge.dao.RuntimeDao;
import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.error.UnsupportedLanguageError;
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
import java.util.stream.Collectors;

/**
 * 判题模块
 */
@Slf4j
@Component
public class Judgement {
    private static final String MEM_LIMIT = "65535";
    private static final String MEM_LIMIT_JVM = "131071";       // For Java/Kotlin

    private static final int AC = 0;
    private static final int TLE = 2;
    private static final int MLE = 3;
    private static final int RE = 4;

    @Value("${project.file-dir}")
    private String fileDir;

    @Value("${project.code-dir}")
    private String codeDir;

    @Value("${project.runner-image}")
    private String runnerImage;

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

    private static class JudgeError extends Exception {
        JudgeError(String msg) {
            super(msg);
        }
    }

    /**
     * 判题
     *
     * @param solution {@link Solution}
     */
    public void judge(Solution solution) {
        long timeout = runtimeDao.getTimeout(solution.getProblemId());

        log.debug("正在判题: solutionId={}.", solution.getSolutionId());

        Runtime runtime = new Runtime(solution.getSolutionId());
        runtimeDao.add(runtime);

        List<RunResult> results = execute(solution, runtime, timeout);
        calcResult(solution, runtime, results);
    }

    /**
     * Calculate results
     *
     * @param results List of {@link RunResult}
     */
    private void calcResult(Solution solution, Runtime runtime, List<RunResult> results) {
        if (runtime.getResult() == SolutionResult.JUDGE_ERROR
                || runtime.getResult() == SolutionResult.RUNTIME_ERROR) {
            solution.setResult(runtime.getResult());
        } else {
            long time = 0;
            long memory = 0;
            int ac = 0;
            int tle = 0;
            int mle = 0;
            int re = 0;

            for (RunResult result : results) {
                switch (result.getStatus()) {
                    case AC:
                        ac++;
                        break;
                    case TLE:
                        tle++;
                        break;
                    case MLE:
                        mle++;
                        break;
                    case RE:
                        re++;
                        break;
                }

                time = result.getTimeUsed() > time ? result.getTimeUsed() : time;
                memory = result.getMemUsed() > memory ? result.getMemUsed() : memory;
            }

            int total = results.size();
            double passRate = 0;
            SolutionResult result;

            runtime.setTotal(total);
            runtime.setPassed(ac);
            runtime.setTime(time);
            runtime.setMemory(memory);

            if (ac == 0) {
                result = SolutionResult.WRONG;
            } else if (ac < total) {
                passRate = (double) ac / total;
                result = SolutionResult.PARTLY_PASSED;
            } else {
                passRate = 1;
                result = SolutionResult.PASSED;
            }

            if (tle != 0)
                result = SolutionResult.TIMEOUT;
            if (mle != 0)
                result = SolutionResult.OOM;
            if (re != 0)
                result = SolutionResult.RUNTIME_ERROR;

            solution.setResult(result);
            solution.setPassRate(Double.isNaN(passRate) ? 0 : passRate);
        }

        solution.setState(SolutionState.JUDGED);
        runtimeDao.update(runtime);
        solutionDao.update(solution);
    }

    /**
     * Execute the compiled code
     *
     * @return List of {@link RunResult}
     */
    private List<RunResult> execute(Solution solution, Runtime runtime, long timeout) {
        List<RunResult> results = null;

        try {
            String testDataDir = fileDir + "test_data/" + solution.getProblemId();
            int outputCount = getOutputCount(solution.getProblemId());
            if (outputCount == 0) {
                throw new JudgeError(String.format("缺少测试数据: problemId=%d", solution.getProblemId()));
            }
            ProcessBuilder cmd = buildCommand(solution, String.valueOf(timeout), testDataDir);
            results = run(cmd, solution.getSolutionId());
        } catch (RuntimeError e) {
            log.error("Runtime Error: {}", e.getMessage());
            runtime.setInfo(e.getMessage());
            runtime.setResult(SolutionResult.RUNTIME_ERROR);
        } catch (JudgeError | InterruptedException | IOException | UnsupportedLanguageError e) {
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
    private List<RunResult> run(ProcessBuilder cmd, String solutionId)
            throws RuntimeError, IOException, InterruptedException {
        Process process = cmd.start();
        process.waitFor();

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
    private ProcessBuilder buildCommand(Solution solution, String timeout, String testDataDir)
            throws UnsupportedLanguageError {
        Language language = Language.get(solution.getLanguage());

        if (language == null) {
            throw new UnsupportedLanguageError("不支持的语言: null.");
        }

        String solutionDir = codeDir + solution.getSolutionId();
        String dataDirInContainer = UUID.randomUUID().toString();   // Use UUID for test data directory in container.

        ProcessBuilder builder = new ProcessBuilder();

        List<String> cmd = new ArrayList<>(Arrays.asList(
                "docker", "run", "--rm", "--network", "none",
                "-v", String.format("%s:/tmp/code", solutionDir),
                "-v", String.format("%s:/%s:ro", testDataDir, dataDirInContainer),
                "-w", "/tmp/code",
                runnerImage, "runner"
        ));

        switch (language) {
            case C:
            case CPP:
                cmd.addAll(Arrays.asList("./Solution", timeout, MEM_LIMIT, MEM_LIMIT));
                break;
            case JAVA:
                cmd.addAll(Arrays.asList("java@-Xms8m@-Xmx64m@Solution", timeout, MEM_LIMIT, MEM_LIMIT_JVM));
                break;
            case PYTHON:
                cmd.addAll(Arrays.asList("python3@Solution.py", timeout, MEM_LIMIT, MEM_LIMIT));
                break;
            case BASH:
                cmd.addAll(Arrays.asList("sh@Solution.sh", timeout, MEM_LIMIT, MEM_LIMIT));
                break;
            case C_SHARP:
                cmd.addAll(Arrays.asList("mono@Solution.exe", timeout, MEM_LIMIT, MEM_LIMIT));
                break;
            case JAVA_SCRIPT:
                cmd.addAll(Arrays.asList("node@Solution.js", timeout, MEM_LIMIT, MEM_LIMIT));
                break;
            case KOTLIN:
                cmd.addAll(Arrays.asList("kotlin@SolutionKt", timeout, MEM_LIMIT, MEM_LIMIT_JVM));
                break;
            default:
                throw new UnsupportedLanguageError(String.format("不支持的语言: %s.", language));
        }

        cmd.add(String.format("/%s", dataDirInContainer));
        builder.command(cmd);

        return builder;
    }

    /**
     * 获取输出数据个数
     */
    private int getOutputCount(int problemId) {
        String testDataDir = fileDir + "test_data/";
        File dir = new File(testDataDir + problemId);

        File[] files = dir.listFiles(pathname -> {
            String name = pathname.getName();
            return name.substring(name.lastIndexOf('.')).equals(".out");
        });

        return files == null ? 0 : files.length;
    }
}

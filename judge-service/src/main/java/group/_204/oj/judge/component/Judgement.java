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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Judgement {
    private static final String MEM_LIMIT = "64";           // 内存限制，单位：MB
    private static final String MAX_MEM_LIMIT = "512";      // 最大（实际）内存限制，单位：MB
    private static final String OUTPUT_LIMIT = "8182";      // 输出限制，单位：KB

    private static final int AC = 0;
    private static final int TLE = 2;
    private static final int MLE = 3;
    private static final int OLE = 4;

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

    /**
     * 判题
     *
     * @param solution {@link Solution}
     */
    public void judge(Solution solution) {
        log.info("Judging: solutionId=[{}], user=[{}].", solution.getSolutionId(), solution.getUserId());

        long timeout = runtimeDao.getTimeout(solution.getProblemId());
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
        if (runtime.getResult() == SolutionResult.IE
                || runtime.getResult() == SolutionResult.RE) {
            solution.setResult(runtime.getResult());
        } else {
            long time = 0;
            long memory = 0;
            int[] resultCount = {0, 0, 0, 0, 0};

            for (RunResult result : results) {
                resultCount[result.getStatus()]++;
                time = result.getTimeUsed() > time ? result.getTimeUsed() : time;
                memory = result.getMemUsed() > memory ? result.getMemUsed() : memory;
            }

            int total = results.size();
            double passRate = 0;
            SolutionResult result;

            if (resultCount[AC] == 0) {
                result = SolutionResult.WA;
            } else if (resultCount[AC] < total) {
                passRate = (double) resultCount[AC] / total;
                result = SolutionResult.PA;
            } else {
                passRate = 1;
                result = SolutionResult.AC;
            }

            if (resultCount[OLE] != 0) {
                result = SolutionResult.OLE;
            } else if (resultCount[MLE] != 0) {
                result = SolutionResult.MLE;
            } else if (resultCount[TLE] != 0) {
                result = SolutionResult.TLE;
            }

            runtime.setTotal(total);
            runtime.setPassed(resultCount[AC]);
            runtime.setTime(time);
            runtime.setMemory(memory);
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
            ProcessBuilder cmd = buildCommand(solution, String.valueOf(timeout), testDataDir);
            results = run(cmd, solution.getSolutionId());
        } catch (RuntimeError e) {
            log.error("Runtime Error: {}", e.getMessage());
            runtime.setInfo(e.getMessage());
            runtime.setResult(SolutionResult.RE);
        } catch (InterruptedException | IOException | UnsupportedLanguageError e) {
            log.error("Judge Error: {}", e.getMessage());
            runtime.setResult(SolutionResult.IE);
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
        List<RunResult> results;

        if (process.waitFor(10, TimeUnit.SECONDS)) {
            if (process.exitValue() == 0) {
                String solutionDir = codeDir + solutionId;
                String resultStr = getResultFromFile(solutionDir + "/result.json");
                results = objectMapper.readValue(resultStr, new TypeReference<List<RunResult>>() {
                });
            } else {
                String stderr = getOutput(process.getErrorStream());
                throw new RuntimeError(stderr);
            }
        } else {
            process.destroy();
            throw new InterruptedException("Wait too long.");
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
    private String getResultFromFile(String filePath) {
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
            throw new UnsupportedLanguageError("Unsupported language: null.");
        }

        String solutionDir = codeDir + solution.getSolutionId();
        String dataDirInContainer = UUID.randomUUID().toString();   // Use UUID for test data directory in container.

        ProcessBuilder builder = new ProcessBuilder();

        List<String> cmd = new ArrayList<>(Arrays.asList(
                "docker", "run", "--rm",
                "--network", "none",
                "-v", String.format("%s:/tmp/code", solutionDir),
                "-v", String.format("%s:/%s:ro", testDataDir, dataDirInContainer),
                "-w", "/tmp/code",
                runnerImage, "runner"
        ));

        switch (language) {
            case C:
            case CPP:
                cmd.add("./Solution");
                break;
            case JAVA:
                cmd.add(String.format("java@-Xms16m@-Xmx%sm@Solution", MAX_MEM_LIMIT));
                break;
            case PYTHON:
                cmd.add("python3@Solution.py");
                break;
            case BASH:
                cmd.add("sh@Solution.sh");
                break;
            case C_SHARP:
                cmd.add("mono@Solution.exe");
                break;
            case JAVA_SCRIPT:
                cmd.add("node@Solution.js");
                break;
            case KOTLIN:
                cmd.add("kotlin@SolutionKt");
                break;
            default:
                throw new UnsupportedLanguageError(String.format("Unsupported language: %s.", language));
        }

        cmd.addAll(Arrays.asList(timeout, MEM_LIMIT, MAX_MEM_LIMIT, OUTPUT_LIMIT));
        cmd.add(String.format("/%s", dataDirInContainer));
        builder.command(cmd);

        return builder;
    }
}

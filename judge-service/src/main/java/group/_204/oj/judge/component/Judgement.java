package group._204.oj.judge.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import group._204.oj.judge.dao.RuntimeDao;
import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.model.*;
import group._204.oj.judge.model.Runtime;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
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

    /**
     * 判题
     *
     * @param solution {@link Solution} 答案对象
     */
    public void judge(Solution solution) {
        long timeout = runtimeDao.getTimeout(solution.getProblemId());
        List<File> input = getInputFiles(solution.getProblemId());
        List<String> expect = getOutputData(solution.getProblemId());

        log.info("Judging: solutionId={}", solution.getSolutionId());

        Runtime runtime = new Runtime(solution.getSolutionId());
        runtimeDao.add(runtime);

        List<RunResult> results = execute(solution, input, runtime, timeout);
        compareResult(solution, runtime, expect, results, timeout);
    }

    /**
     * Calculate results
     *
     * @param expect  Correct output
     * @param results List of run result
     */
    private void compareResult(Solution solution, Runtime runtime, List<String> expect, List<RunResult> results, long timeout) {
        int total = expect.size();
        int passed = 0;

        if (runtime.getResult() == SolutionResult.JUDGE_ERROR.ordinal()
                || runtime.getResult() == SolutionResult.RUNTIME_ERROR.ordinal()) {
            solution.setResult(runtime.getResult());
        } else {
            int size = Math.min(results.size(), expect.size());
            Long time = 0L;
            Long memory = 0L;

            // Compare output
            for (int i = 0; i < size; i++) {
                RunResult res = results.get(i);

                if (time < res.getTimeUsed()) {
                    time = res.getTimeUsed();
                }

                if (memory < res.getMemUsed()) {
                    memory = res.getMemUsed();
                }

                if (res.getStatus() == 0 && expect.get(i).length() == res.getStdout().length()
                        && expect.get(i).equals(res.getStdout())) {
                    passed++;
                }
            }

            runtime.setTotal(total);
            runtime.setPassed(passed);
            runtime.setTime(time);
            runtime.setMemory(memory);

            runtimeDao.update(runtime);

            if (passed == 0) {
                solution.setResult(SolutionResult.WRONG.ordinal());
            } else if (passed < total) {
                if (time > timeout) {
                    // TIME OUT
                    solution.setResult(SolutionResult.TIMEOUT.ordinal());
                } else if (memory > Long.parseLong(MEM_LIMIT)) {
                    // OUT OF MEMORY LIMIT
                    solution.setResult(SolutionResult.OUT_OF_MEM.ordinal());
                } else {
                    // PARTLY PASSED
                    solution.setResult(SolutionResult.PARTLY_PASSED.ordinal());
                }
            } else {
                solution.setResult(SolutionResult.ALL_PASSED.ordinal());
            }

            double passRate = (double) passed / total;
            solution.setPassRate(Double.isNaN(passRate) ? 0 : passRate);
        }

        solution.setState(SolutionState.JUDGED.ordinal());
        solutionDao.update(solution);
    }

    /**
     * Execute the compiled code
     *
     * @param inputFiles File of input
     * @return List of run result
     */
    private List<RunResult> execute(Solution solution, @Nullable List<File> inputFiles, Runtime runtime, long timeout) {
        List<RunResult> results = new ArrayList<>();

        try {
            // No input file
            if (inputFiles == null) {
                ProcessBuilder cmd = buildCommand(solution, String.valueOf(timeout), MEM_LIMIT, MAX_MEM_LIMIT, null);

                assert cmd != null;
                RunResult result = run(cmd, solution.getSolutionId());
                results.add(result);
            } else {
                long maxTime = 0;

                // Run code with each input file
                for (File input : inputFiles) {
                    ProcessBuilder cmd = buildCommand(solution, String.valueOf(timeout), MEM_LIMIT, MAX_MEM_LIMIT,
                            input.getAbsolutePath());
                    assert cmd != null;
                    RunResult result = run(cmd, solution.getSolutionId());
                    results.add(result);
                }

                runtime.setTime(maxTime);
            }
        } catch (RuntimeError e) {
            log.error("Judge Error: {}", e.getMessage());
            runtime.setInfo(e.getMessage());
            runtime.setResult(SolutionResult.RUNTIME_ERROR.ordinal());
        } catch (InterruptedException | IOException | TimeoutError e) {
            log.error("Judge Error: {}", e.getMessage());
            runtime.setResult(SolutionResult.JUDGE_ERROR.ordinal());
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
    private RunResult run(ProcessBuilder cmd, String solutionId) throws RuntimeError, TimeoutError, IOException, InterruptedException {
        Process process = cmd.start();

        if (!process.waitFor(6000, TimeUnit.MILLISECONDS)) {
            throw new TimeoutError("Wait timeout.");
        }

        RunResult result;
        String stderr = getOutput(process.getErrorStream());

        if (stderr.isEmpty()) {
            String solutionDir = codeDir + solutionId;
            // Get run result
            String stdout = getOutputFromFile(solutionDir + "/result.out");
            result = objectMapper.readValue(stdout, RunResult.class);
            // stdout of solution
            File file = new File(solutionDir + "/output.out");
            // Read output file
            result.setStdout(getOutput(new FileInputStream(file)));

            if (!file.delete()) {
                log.error("Delete stdout failed: {}", file.getAbsolutePath());
            }
        } else {
            log.error(stderr);
            throw new RuntimeError(stderr);
        }

        return result;
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
    private ProcessBuilder buildCommand(Solution solution, String timeout, String memLimit, String maxMemLimit, String in) {
        Language language = Language.get(solution.getLanguage());
        assert language != null;

        String solutionDir = codeDir + solution.getSolutionId();

        ProcessBuilder builder = new ProcessBuilder();

        List<String> cmd = new ArrayList<>(Arrays.asList(
                "docker", "run", "--rm", "--network", "none", "-v", solutionDir + ":" + solutionDir,
                "-v", fileDir + ":" + fileDir + ":ro", "-w", solutionDir, runnerImage, "runner"
        ));

        if (in == null)
            in = "/dev/null";

        switch (language) {
            case C:
            case CPP:
                cmd.addAll(Arrays.asList("./Solution", timeout, memLimit, memLimit, in));
                builder.command(cmd);
                return builder;
            case JAVA:
                cmd.addAll(Arrays.asList("java@Solution", timeout, memLimit, maxMemLimit, in));
                builder.command(cmd);
                return builder;
            case PYTHON:
                cmd.addAll(Arrays.asList("python@Solution.py", timeout, memLimit, memLimit, in));
                builder.command(cmd);
                return builder;
            case BASH:
                cmd.addAll(Arrays.asList("sh@Solution.sh", timeout, memLimit, memLimit, in));
                builder.command(cmd);
                return builder;
            case C_SHARP:
                cmd.addAll(Arrays.asList("mono@Solution.exe", timeout, memLimit, memLimit, in));
                builder.command(cmd);
                return builder;
            default:
                return null;
        }
    }

    /**
     * Get input files
     */
    private List<File> getInputFiles(int problemId) {
        String testDataDir = fileDir + "test_data/";
        File dir = new File(testDataDir + problemId);

        File[] files = dir.listFiles(pathname -> {
            String name = pathname.getName();
            return name.substring(name.lastIndexOf('.')).equals(".in");
        });

        if (files != null && files.length > 0) {
            return Arrays.stream(files)
                    .sorted(Comparator.comparing(File::getName))
                    .collect(Collectors.toList());
        }

        return null;
    }

    /**
     * 获取测试输出数据
     */
    private List<String> getOutputData(int problemId) {
        List<String> data = new ArrayList<>();

        String testDataDir = fileDir + "test_data/";
        File dir = new File(testDataDir + problemId);

        File[] files = dir.listFiles(pathname -> {
            String name = pathname.getName();
            return name.substring(name.lastIndexOf('.')).equals(".out");
        });

        if (files != null) {
            List<File> fileList = Arrays.stream(files)
                    .sorted(Comparator.comparing(File::getName))
                    .collect(Collectors.toList());

            for (File file : fileList) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    data.add(reader.lines().collect(Collectors.joining("\n")));
                } catch (FileNotFoundException e) {
                    log.error(e.getMessage());
                }
            }
        }

        return data;
    }
}

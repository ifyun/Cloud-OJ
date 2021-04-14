package group._204.oj.judge.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import group._204.oj.judge.dao.*;
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
import org.springframework.transaction.annotation.Transactional;

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
    private static final Integer OUTPUT_LIMIT = 16;
    private static final Integer MAX_MEM_LIMIT = 512;

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
    private CompileDao compileDao;

    @Resource
    private ProblemDao problemDao;

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private DatabaseConfig dbConfig;

    @Resource
    private Compiler compiler;

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
    @Transactional(rollbackFor = Exception.class)
    public void judge(Solution solution) {
        log.info("Judging: solution({}), user({}).", solution.getSolutionId(), solution.getUserId());
        dbConfig.disableFKChecks(); // 为当前事务禁用外键约束

        Compile compile = compiler.compile(solution);
        compileDao.add(compile);

        if (compile.getState() == 0) {
            Limit limit = problemDao.getLimit(solution.getProblemId());
            Runtime runtime = new Runtime(solution.getSolutionId());
            runtimeDao.add(runtime);

            List<RunResult> results = execute(solution, runtime, limit);
            calcResult(solution, runtime, results);
        } else {
            solution.setResult(SolutionResult.CE);
        }

        solution.setState(SolutionState.JUDGED);
        solutionDao.update(solution);
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
    private List<RunResult> execute(Solution solution, Runtime runtime, Limit limit) {
        List<RunResult> results = null;

        try {
            String testDataDir = fileDir + "test_data/" + solution.getProblemId();
            ProcessBuilder cmd = buildCommand(solution, limit, testDataDir);
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
            int exitValue = process.exitValue();
            if (exitValue == 0) {
                String solutionDir = codeDir + solutionId;
                String resultStr = getResultFromFile(solutionDir + "/result.json");
                results = objectMapper.readValue(resultStr, new TypeReference<List<RunResult>>() {
                });
            } else {
                String stderr = getOutput(process.getErrorStream());
                if (exitValue == 1) {
                    throw new RuntimeError(stderr);
                } else {
                    throw new InterruptedException(stderr);
                }
            }
        } else {
            process.destroy();
            throw new InterruptedException("Judge timeout.");
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
    private ProcessBuilder buildCommand(Solution solution, Limit limit, String testDataDir)
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

        long timeLimit = limit.getTimeout();
        int memoryLimit = limit.getMemoryLimit();
        int maxMemoryLimit = memoryLimit << 2;

        // Java/Kotlin/JS 内存限制按 2 倍计算
        switch (language) {
            case C:
            case CPP:
            case GO:
                maxMemoryLimit <<= 1;
                cmd.add("./Solution");
                break;
            case JAVA:
                memoryLimit <<= 1;
                maxMemoryLimit = memoryLimit << 2;
                cmd.add(String.format("java@-Xmx%dm@Solution", memoryLimit << 1));
                break;
            case KOTLIN:
                timeLimit <<= 1;
                memoryLimit <<= 1;
                maxMemoryLimit = memoryLimit << 2;
                cmd.add("kotlin@SolutionKt");
                break;
            case JAVA_SCRIPT:
                memoryLimit <<= 1;
                cmd.add("node@Solution.js");
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
            default:
                throw new UnsupportedLanguageError(String.format("Unsupported language: %s.", language));
        }

        if (maxMemoryLimit >= MAX_MEM_LIMIT) {
            maxMemoryLimit = MAX_MEM_LIMIT;
        }

        List<String> limits = Arrays.asList(
                Long.toString(timeLimit),
                Integer.toString(memoryLimit),
                Integer.toString(maxMemoryLimit),
                OUTPUT_LIMIT.toString()
        );

        cmd.addAll(limits);
        cmd.add(String.format("/%s", dataDirInContainer));
        builder.command(cmd);

        return builder;
    }
}

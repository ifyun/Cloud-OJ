package group._204.oj.judge.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import group._204.oj.judge.dao.*;
import group._204.oj.judge.error.UnsupportedLanguageError;
import group._204.oj.judge.model.*;
import group._204.oj.judge.model.Runtime;
import group._204.oj.judge.type.Language;
import group._204.oj.judge.type.SolutionResult;
import group._204.oj.judge.type.SolutionState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@Slf4j
@Component
public class Judgement {
    private static final Integer OUTPUT_LIMIT = 16;
    private static final Integer MAX_MEM_LIMIT = 512;

    @Value("${project.file-dir}")
    private String fileDir;

    @Value("${project.code-dir}")
    private String codeDir;

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

    @PostConstruct
    private void init() {
        if (!codeDir.endsWith("/")) {
            codeDir += '/';
        }
        if (!fileDir.endsWith("/")) {
            fileDir += '/';
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

        Compile compile = compiler.compile(solution);
        compileDao.add(compile);

        if (compile.getState() == 0) {
            Limit limit = problemDao.getLimit(solution.getProblemId());
            Runtime runtime = new Runtime(solution.getSolutionId());
            runtimeDao.add(runtime);

            RunResult result = execute(solution, runtime, limit);
            saveResult(solution, runtime, result);
        } else {
            solution.setResult(SolutionResult.CE);
        }

        solution.setState(SolutionState.JUDGED);
        solutionDao.update(solution);
    }

    /**
     * 保存结果
     *
     * @param result {@link RunResult}
     */
    private void saveResult(Solution solution, Runtime runtime, RunResult result) {
        if (runtime.getResult() == SolutionResult.IE || runtime.getResult() == SolutionResult.RE) {
            solution.setResult(runtime.getResult());
        } else {
            runtime.setTotal(result.getTotal());
            runtime.setPassed(result.getPassed());
            runtime.setTime(result.getTime());
            runtime.setMemory(result.getMemory());
            solution.setResult(SolutionResult.getByString(result.getResult()));
            Double passRate = result.getPassRate();
            solution.setPassRate(Double.isNaN(passRate) ? 0 : passRate);
        }

        solution.setState(SolutionState.JUDGED);
        runtimeDao.update(runtime);
        solutionDao.update(solution);
    }

    /**
     * 执行用户程序
     *
     * @return 运行结果 {@link RunResult}
     */
    private RunResult execute(Solution solution, Runtime runtime, Limit limit) {
        RunResult result = null;

        try {
            String testDataDir = fileDir + "test_data/" + solution.getProblemId();
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
        Process process = cmd.start();

        int exitValue = process.waitFor();

        if (exitValue == 0) {
            // 正常退出
            String resultStr = IOUtils.toString(process.getInputStream());
            result = objectMapper.readValue(resultStr, RunResult.class);
        } else {
            // 非正常退出
            String stderr = IOUtils.toString(process.getErrorStream());
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
        Language language = Language.get(solution.getLanguage());

        if (language == null) {
            throw new UnsupportedLanguageError("Unsupported language: null.");
        }

        String solutionDir = codeDir + solution.getSolutionId();

        ProcessBuilder builder = new ProcessBuilder();

        List<String> cmd = new ArrayList<>();
        cmd.add("/opt/bin/judge-runner");

        long timeLimit = limit.getTimeout();
        int memoryLimit = limit.getMemoryLimit();
        int maxMemoryLimit = memoryLimit << 2;
        int procLimit = 1;

        // Java/Kotlin/JS 内存限制按 2 倍计算
        switch (language) {
            case C:
            case CPP:
                maxMemoryLimit <<= 1;
                cmd.add("--cmd=./Solution");
                break;
            case GO:
                procLimit = 10;
                maxMemoryLimit <<= 1;
                cmd.add("--cmd=./Solution");
                break;
            case JAVA:
                procLimit = 15;
                memoryLimit <<= 1;
                maxMemoryLimit = memoryLimit << 2;
                cmd.add(String.format("--cmd=java@-Xmx%dm@Solution", memoryLimit << 1));
                break;
            case KOTLIN:
                procLimit = 20;
                timeLimit <<= 1;
                memoryLimit <<= 1;
                maxMemoryLimit = memoryLimit << 2;
                cmd.add("--cmd=kotlin@SolutionKt");
                break;
            case JAVA_SCRIPT:
                procLimit = 10;
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
                procLimit = 5;
                memoryLimit <<= 1;
                cmd.add("--cmd=mono@Solution.exe");
                break;
            default:
                throw new UnsupportedLanguageError(language.toString());
        }

        if (maxMemoryLimit >= MAX_MEM_LIMIT) {
            maxMemoryLimit = MAX_MEM_LIMIT;
        }

        List<String> config = Arrays.asList(
                "--time=" + timeLimit,
                "--memory=" + memoryLimit,
                "--max-memory=" + maxMemoryLimit,
                "--output-size=" + OUTPUT_LIMIT,
                "--workdir=" + solutionDir,
                "--data=" + testDataDir,
                "--proc=" + procLimit
        );

        cmd.addAll(config);
        builder.command(cmd);
        return builder;
    }
}

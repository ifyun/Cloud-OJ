package group._204.oj.judge.component;

import group._204.oj.judge.JudgeServiceApplication;
import group._204.oj.judge.dao.RuntimeDao;
import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.model.*;
import group._204.oj.judge.model.Runtime;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.io.*;
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

    @Value("${project.target-dir}")
    private String targetDir;

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
        List<String> input = getInputData(solution.getProblemId());
        List<String> expect = getOutputData(solution.getProblemId());

        ProcessBuilder cmd = buildCommand(solution);
        log.info("正在执行 solutionId: {}", solution.getSolutionId());

        Runtime runtime = new Runtime(solution.getSolutionId());
        runtimeDao.add(runtime);
        // 执行并获取输出
        List<String> output = execute(Objects.requireNonNull(cmd), input, runtime, timeout);
        // 比较结果
        compareResult(solution, runtime, expect, output, timeout);
    }

    /**
     * 比较结果
     *
     * @param solution {@link Solution} 答案对象
     * @param expect   期望输出
     * @param output   实际输出
     */
    private void compareResult(Solution solution, Runtime runtime, List<String> expect, List<String> output, long timeout) {
        int total = expect.size();
        int passed = 0;

        if (runtime.getResult() == SolutionResult.JUDGE_ERROR.ordinal()
                || runtime.getResult() == SolutionResult.RUNTIME_ERROR.ordinal()) {
            solution.setResult(runtime.getResult());
        } else if (runtime.getTime() > timeout) {
            solution.setResult(SolutionResult.TIMEOUT.ordinal());
        } else {
            int size = Math.min(output.size(), expect.size());
            for (int i = 0; i < size; i++) {
                if (expect.get(i).equals(output.get(i)))
                    passed++;
            }

            runtime.setTotal(total);
            runtime.setPassed(passed);
            runtimeDao.update(runtime);

            if (passed == 0) {
                solution.setResult(SolutionResult.WRONG.ordinal());
            } else if (passed < total) {
                solution.setResult(SolutionResult.PARTLY_PASSED.ordinal());
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
     * 执行编译后的程序
     *
     * @param input 输入数据
     * @return 程序执行结果
     */
    private List<String> execute(ProcessBuilder cmd, List<String> input, Runtime runtime, long timeout) {
        List<String> output = new ArrayList<>();

        try {
            StopWatch stopWatch = new StopWatch("GET-EXECUTE-TIME");
            log.info("cmd: {}", cmd.command());

            if (input.size() == 0) {
                // 执行程序
                Process process = cmd.start();
                stopWatch.start();

                if (!process.waitFor(timeout + 150, TimeUnit.MILLISECONDS)) {
                    throw new InterruptedException("时间超限.");
                }

                stopWatch.stop();
                // 计算运行时间
                runtime.setTime(stopWatch.getTotalTimeMillis());

                // 获取标准错误流
                String stderr = getOutput(process.getErrorStream());
                if (stderr.isEmpty()) {
                    // 获取标准输出流
                    String stdout = getOutput(process.getInputStream());
                    output.add(stdout);
                } else {
                    throw new RuntimeError(stderr);
                }
            } else {
                long maxTime = 0;

                for (String s : input) {
                    Process process = cmd.start();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                    // 输入数据
                    writer.write(s);
                    writer.close();

                    stopWatch.start();

                    if (!process.waitFor(timeout + 150, TimeUnit.MILLISECONDS)) {
                        throw new TimeoutError("时间超限.");
                    }

                    stopWatch.stop();

                    long time = stopWatch.getTotalTimeMillis();
                    maxTime = Math.max(time, maxTime);

                    // 获取标准错误流
                    String stderr = getOutput(process.getErrorStream());
                    if (stderr.isEmpty()) {
                        // 获取标准输出流
                        String stdout = getOutput(process.getInputStream());
                        output.add(stdout);
                    } else {
                        throw new RuntimeError(stderr);
                    }
                }

                runtime.setTime(maxTime);
            }
        } catch (RuntimeError e) {
            output.clear();
            runtime.setInfo(e.getMessage());
            runtime.setResult(SolutionResult.RUNTIME_ERROR.ordinal());
        } catch (TimeoutError e) {
            log.error(e.getMessage());
            runtime.setTime(timeout);
        } catch (InterruptedException | IOException e) {
            log.error(e.getMessage());
            runtime.setResult(SolutionResult.JUDGE_ERROR.ordinal());
            output.clear();
        }

        runtime.setOutput(String.join("\n", output));
        runtimeDao.update(runtime);
        return output;
    }

    @SneakyThrows
    private String getOutput(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String output = reader.lines().collect(Collectors.joining("\n"));
        reader.close();
        return output;
    }

    /**
     * 构造执行命令
     *
     * @param solution {@link Solution} 答案对象
     * @return {@link ProcessBuilder}
     */
    private ProcessBuilder buildCommand(Solution solution) {
        Language language = Language.get(solution.getLanguage());
        assert language != null;

        String filePath = targetDir + solution.getSolutionId();
        ProcessBuilder builder = new ProcessBuilder();

        switch (language) {
            case C:
            case CPP:
                // NOTE Windows 和 Linux 生成的目标文件不一样
                if (JudgeServiceApplication.isWindows) {
                    builder.command(filePath + ".exe");
                } else {
                    builder.command(filePath);
                }
                return builder;
            case JAVA:
                builder.command("java", "Solution");
                // NOTE Java 语言必须切换到 class 文件所在目录
                builder.directory(new File(filePath));
                return builder;
            case PYTHON:
                builder.command("python", filePath + ".py");
                return builder;
            case BASH:
                builder.command("sh", filePath + ".sh");
                return builder;
            case C_SHARP:
                builder.command("mono", filePath + "/Solution.exe");
                return builder;
            default:
                return null;
        }
    }

    /**
     * 获取测试输入数据
     *
     * @param problemId 题目 Id
     * @return 每一组输入数据
     */
    private List<String> getInputData(int problemId) {
        return getData(problemId, ".in");
    }

    /**
     * 获取测试输出数据
     *
     * @param problemId 题目 Id
     * @return 每一组输出数据
     */
    private List<String> getOutputData(int problemId) {
        return getData(problemId, ".out");
    }

    /**
     * 读取测试数据
     *
     * @param problemId 题目 Id
     * @param ext       文件扩展名
     * @return 测试数据集合
     */
    private List<String> getData(int problemId, String ext) {
        List<String> data = new ArrayList<>();
        String testDataDir = fileDir + "test_data/";
        File dir = new File(testDataDir + problemId);
        File[] files = dir.listFiles(pathname -> {
            String name = pathname.getName();
            return name.substring(name.lastIndexOf('.')).equals(ext);
        });

        if (files != null) {
            // 按文件名排序
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
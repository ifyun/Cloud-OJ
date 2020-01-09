package top.cloudli.judgeservice.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.dao.RuntimeDao;
import top.cloudli.judgeservice.dao.SolutionDao;
import top.cloudli.judgeservice.model.Language;
import top.cloudli.judgeservice.model.Runtime;
import top.cloudli.judgeservice.model.Solution;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Judgement {

    @Value("${project.test-data-dir}")
    private String testDataDir;

    @Value("${project.target-dir}")
    private String targetDir;

    @Resource
    private RuntimeDao runtimeDao;

    @Resource
    private SolutionDao solutionDao;

    private ProcessBuilder processBuilder = new ProcessBuilder();

    private boolean isLinux;

    @PostConstruct
    public void init() {
        isLinux = System.getProperty("os.name").equals("Linux");
    }

    /**
     * 判题
     *
     * @param solution {@link Solution}
     */
    public void judge(Solution solution) {
        List<String> input = getInputData(solution.getProblemId());
        List<String> expect = getOutputData(solution.getProblemId());

        String[] cmd = buildCommand(solution);
        log.info("正在执行, solutionId: {}", solution.getSolutionId());
        List<String> output = execute(cmd, input);  // 获取输出

        compareResult(solution, expect, output);
    }

    /**
     * 比较结果
     *
     * @param solution {@link Solution}
     * @param expect   期望输出
     * @param output   实际输出
     */
    private void compareResult(Solution solution, List<String> expect, List<String> output) {
        int total = expect.size();
        int passed = 0;

        for (int i = 0; i < output.size(); i++) {
            if (expect.get(i).equals(output.get(i)))
                passed++;
        }

        Runtime runtime = new Runtime(solution.getSolutionId(), total, passed);
        runtimeDao.add(runtime);

        if (passed == 0)
            solution.setResult(2);
        else if (passed < total)
            solution.setResult(1);
        else {
            solution.setResult(0);
        }

        solution.setPassRate(((double) passed) / total);
        solution.setState(0);
        solutionDao.update(solution);
    }

    /**
     * 执行
     *
     * @param input 输入数据
     * @return 程序执行结果
     */
    private List<String> execute(String[] cmd, List<String> input) {
        List<String> output = new ArrayList<>();
        processBuilder.command(cmd);

        try {
            if (input.size() == 0) {
                // 执行程序
                Process process = processBuilder.start();
                // 获取错误流
                String error = getOutput(process.getErrorStream());

                if (error.isEmpty()) {
                    // 获取标准输出流
                    String temp = getOutput(process.getInputStream());
                    output.add(temp);
                }
            } else {
                for (String s : input) {
                    Process process = processBuilder.start();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                    // 输入数据
                    writer.write(s);
                    writer.close();

                    String error = getOutput(process.getErrorStream());

                    if (error.isEmpty()) {
                        String temp = getOutput(process.getInputStream());
                        output.add(temp);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return output;
    }

    private String getOutput(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    /**
     * 构造执行命令
     *
     * @param solution {@link Solution}
     * @return 执行命令
     */
    private String[] buildCommand(Solution solution) {
        Language language = Language.get(solution.getLanguage());
        assert language != null;

        switch (language) {
            case C_CPP:
                String filePath = targetDir + solution.getSolutionId();
                return isLinux ? new String[]{filePath + ".o"} : new String[]{filePath + ".exe"};
            case JAVA:
                // TODO Java 执行命令
        }

        return null;
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

    private List<String> getData(int problemId, String ext) {
        List<String> data = new ArrayList<>();

        File dir = new File(testDataDir + problemId);
        File[] files = dir.listFiles(pathname -> {
            String name = pathname.getName();
            return name.substring(name.lastIndexOf('.')).equals(ext);
        });

        if (files == null)
            return data;

        // 按文件名排序
        List<File> fileList = Arrays.stream(files)
                .sorted(Comparator.comparing(File::getName))
                .collect(Collectors.toList());

        for (File file : fileList) {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file))
                );
                data.add(reader.lines().collect(Collectors.joining("\n")));
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            }
        }

        return data;
    }
}

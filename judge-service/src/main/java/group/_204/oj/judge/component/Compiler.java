package group._204.oj.judge.component;

import group._204.oj.judge.error.UnsupportedLanguageError;
import group._204.oj.judge.model.Compile;
import group._204.oj.judge.type.Language;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 编译模块
 */
@Slf4j
@Component
class Compiler {

    @Value("${project.code-dir}")
    private String codeDir;

    @Value("${project.runner-image}")
    private String runnerImage;

    private final ProcessBuilder processBuilder = new ProcessBuilder();

    private static class CompileError extends Exception {
        CompileError(String msg) {
            super(msg);
        }
    }

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        File dir = new File(codeDir);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                log.info("目录 {} 不存在, 已创建", codeDir);
            } else {
                log.info("无法创建目录 {}", codeDir);
            }
        }
    }

    /**
     * 编译入口
     *
     * @param solutionId 答案 Id
     * @param languageId 语言 Id
     * @param source     源代码
     * @return {@link Compile} 编译结果
     */
    public Compile compile(String solutionId, int languageId, String source) {
        String src = writeCode(solutionId, languageId, source);

        if (src.isEmpty()) {
            String info = "无法写入源码";
            log.error("编译异常: {}", info);
            return new Compile(solutionId, -1, info);
        }

        Language language = Language.get(languageId);

        if (language != null) {
            try {
                return compileSource(solutionId, languageId);
            } catch (UnsupportedLanguageError e) {
                log.error("编译异常: {}", e.getMessage());
                return new Compile(solutionId, -1, "不支持的语言");
            }
        } else {
            return new Compile(solutionId, -1, "不支持的语言");
        }
    }

    /**
     * 根据语言类型编译源码
     *
     * @param solutionId 答案 Id
     * @param languageId 语言类型
     * @return {@link Compile} 编译结果
     */
    public Compile compileSource(String solutionId, int languageId) throws UnsupportedLanguageError {
        Language language = Language.get(languageId);

        if (language == null) {
            return new Compile(solutionId, -1, "不支持的语言.");
        }

        String solutionDir = codeDir + solutionId;
        List<String> cmd = new ArrayList<>(Arrays.asList("docker", "run", "--rm", "--network", "none",
                "-v", solutionDir + ":/tmp/code", "-w", "/tmp/code", runnerImage));

        // 构造编译命令
        switch (language) {
            case C:
                cmd.addAll(Arrays.asList("gcc", "Solution.c", "-o", "Solution"));
                break;
            case CPP:
                cmd.addAll(Arrays.asList("g++", "Solution.cpp", "-o", "Solution"));
                break;
            case JAVA:
                cmd.addAll(Arrays.asList("javac", "-encoding", "UTF-8", "Solution.java"));
                break;
            case C_SHARP:
                cmd.addAll(Arrays.asList("mcs", "Solution.cs"));
                break;
            case KOTLIN:
                cmd.addAll(Arrays.asList("kotlinc", "Solution.kt"));
                break;
            case PYTHON:
                return new Compile(solutionId, 0, "Python");
            case BASH:
                return new Compile(solutionId, 0, "Bash");
            case JAVA_SCRIPT:
                return new Compile(solutionId, 0, "JavaScript");
            default:
                throw new UnsupportedLanguageError("Unsupported language.");
        }

        try {
            processBuilder.command(cmd);
            Process process = processBuilder.start();
            process.waitFor(3000, TimeUnit.MILLISECONDS);
            // 获取错误流，为空说明编译成功
            String error = getOutput(process.getErrorStream());

            if (error.isEmpty()) {
                log.info("编译成功: solutionId={}", solutionId);
                return new Compile(solutionId, 0, null);
            } else {
                throw new CompileError(error);
            }
        } catch (IOException | InterruptedException | CompileError e) {
            log.error("编译异常: solutionId={}", e.getMessage());
            return new Compile(solutionId, -1, e.getMessage());
        }
    }

    /**
     * 从 Process 的输入流或错误流获取输出
     *
     * @param inputStream Process 的输入流（stdout or stderr）
     * @return Process 的输出
     */
    @SneakyThrows
    private String getOutput(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String out = reader.lines().collect(Collectors.joining("\n"));
        reader.close();
        return out;
    }

    /**
     * 将源码写入文件
     *
     * @param solutionId 答案 Id
     * @param language   语言 Id
     * @param source     源码
     * @return 文件路径
     */
    private String writeCode(String solutionId, int language, String source) {
        File file;
        File solutionDir = new File(codeDir + solutionId);

        if (!solutionDir.mkdirs()) {
            log.error("无法创建目录 {}", solutionDir.getName());
            return "";
        }

        if (Language.get(language) == Language.JAVA) {
            file = new File(solutionDir + "/Solution.java");
        } else if (Language.get(language) == Language.C_SHARP) {
            file = new File(solutionDir + "/Solution.cs");
        } else {
            file = new File(solutionDir + "/Solution" + Language.getExt(language));
        }

        try {
            if (file.exists() || file.createNewFile()) {
                FileWriter writer = new FileWriter(file, false);
                writer.write(source);
                writer.flush();
                writer.close();

                return file.getPath();
            } else {
                log.error("无法创建文件 {}", file.getName());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return "";
    }
}

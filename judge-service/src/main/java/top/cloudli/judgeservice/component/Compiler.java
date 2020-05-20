package top.cloudli.judgeservice.component;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.JudgeServiceApplication;
import top.cloudli.judgeservice.model.Compile;
import top.cloudli.judgeservice.model.Language;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.stream.Collectors;

/**
 * 编译模块
 */
@Slf4j
@Component
class Compiler {

    @Value("${project.target-dir}")
    private String targetDir;

    private final ProcessBuilder processBuilder = new ProcessBuilder();

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        File dir = new File(targetDir);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                log.info("目录 {} 不存在, 已创建", targetDir);
            } else {
                log.info("无法创建目录 {}", targetDir);
                System.exit(-1);
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
        log.info("正在编译 solutionId: {}, 语言: {}", solutionId, Language.get(languageId));
        String src = writeCode(solutionId, languageId, source);

        if (src.isEmpty())
            return new Compile(solutionId, -1, "无法写入源码");

        Language language = Language.get(languageId);

        if (language != null)
            return compileSource(solutionId, languageId, src);
        else
            return new Compile(solutionId, -1, "不支持的语言");
    }

    /**
     * 根据语言类型编译源码
     *
     * @param solutionId 答案 Id
     * @param languageId 语言类型
     * @param src        源码文件路径
     * @return {@link Compile} 编译结果
     */
    public Compile compileSource(String solutionId, int languageId, String src) {
        Language language = Language.get(languageId);

        if (language == null)
            return new Compile(solutionId, -1, "不支持的语言.");

        // NOTE 构造编译命令
        switch (language) {
            case C:
                processBuilder.command("gcc", src, "-o", src.substring(0, src.indexOf(".")));
                break;
            case CPP:
                processBuilder.command("g++", src, "-o", src.substring(0, src.indexOf(".")));
                break;
            case JAVA:
                processBuilder.command("javac", "-encoding", "UTF-8", src);
                break;
            case PYTHON:
                return new Compile(solutionId, 0, "Python 无需编译.");
            case BASH:
                return new Compile(solutionId, 0, "Shell 无需编译.");
            case C_SHARP:
                processBuilder.command(JudgeServiceApplication.isWindows ? "mcs.bat" : "mcs", src);
        }

        try {
            Process process = processBuilder.start();
            // 获取错误流，为空说明编译成功
            String error = getOutput(process.getErrorStream());

            if (error.isEmpty()) {
                log.info("编译完成: solutionId: {}", solutionId);
                return new Compile(solutionId, 0, "编译成功");
            }

            return new Compile(solutionId, -1, error);
        } catch (IOException e) {
            log.error("编译异常: solutionId: {}", e.getMessage());
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

        if (Language.get(language) == Language.JAVA) {
            // Java 语言文件名和类名必须相同
            File dir = new File(targetDir + solutionId);
            if (dir.mkdirs()) {
                file = new File(targetDir + solutionId + "/Solution.java");
            } else {
                log.error("无法创建目录 {}", dir.getName());
                return "";
            }
        } else if (Language.get(language) == Language.C_SHARP) {
            File dir = new File(targetDir + solutionId);
            if (dir.mkdirs()) {
                file = new File(targetDir + solutionId + "/Solution.cs");
            } else {
                log.error("无法创建目录 {}", dir.getName());
                return "";
            }
        } else {
            file = new File(targetDir + solutionId + Language.getExt(language));
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

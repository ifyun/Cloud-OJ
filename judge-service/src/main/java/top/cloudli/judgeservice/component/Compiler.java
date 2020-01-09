package top.cloudli.judgeservice.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.model.Compile;
import top.cloudli.judgeservice.model.Language;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.stream.Collectors;

@Slf4j
@Component
class Compiler {

    @Value("${project.target-dir}")
    private String targetDir;

    private ProcessBuilder processBuilder = new ProcessBuilder();

    @PostConstruct
    public void init() {
        File dir = new File(targetDir);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                log.info("目录 {} 不存在, 已创建.", targetDir);
            } else {
                log.info("无法创建目录.");
                System.exit(-1);
            }
        }
    }

    public Compile compile(int solutionId, int languageId, String source) {
        log.info("正在编译, solutionId: {}", solutionId);
        String src = writeCode(solutionId, languageId, source);

        if (src.isEmpty())
            return new Compile(solutionId, -1, "无法写入源码.");

        Language language = Language.get(languageId);

        if (language != null)
            return compileSource(solutionId, languageId, src);
        else
            return new Compile(solutionId, -1, "不支持的语言.");
    }

    /**
     * 根据语言类型编译源码
     *
     * @param solutionId 问题 Id
     * @param languageId 语言类型
     * @param src        源码
     * @return 编译信息 {@link Compile}
     */
    public Compile compileSource(int solutionId, int languageId, String src) {
        Language language = Language.get(languageId);

        assert language != null;

        // 构造编译命令
        switch (language) {
            case C_CPP:
                processBuilder.command("g++", src, "-o", src.substring(0, src.indexOf(".")));
                break;
            case JAVA:
                processBuilder.command("javac", src);
                break;
            case PYTHON:
            case BASH:
                return new Compile(solutionId, 0, "无需编译.");
        }

        try {
            Process process = processBuilder.start();
            // 获取错误流，为空说明编译成功
            String error = getOutput(process.getErrorStream());

            if (error.isEmpty()) {
                log.info("编译完成: solutionId: {}", solutionId);
                return new Compile(solutionId, 0, "file: " + src);
            }

            return new Compile(solutionId, -1, error);
        } catch (IOException e) {
            log.error("编译异常: solutionId: {}", e.getMessage());
            return new Compile(solutionId, -1, e.getMessage());
        }
    }

    private String getOutput(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    /**
     * 将源码写入文件
     *
     * @param id       solutionId
     * @param language 语言 Id
     * @param source   源码
     * @return 文件路径
     */
    private String writeCode(int id, int language, String source) {
        File file;

        if (Language.get(language) == Language.JAVA) {
            // Java 语言文件名和类名必须相同
            File dir = new File(targetDir + id);
            if (dir.mkdirs()) {
                file = new File(targetDir + id + "/Solution.java");
            }
            else {
                log.error("无法创建目录 {}", dir.getName());
                return "";
            }
        } else {
            file = new File(targetDir + id + Language.getExt(language));
        }

        try {
            if (file.exists() || file.createNewFile()) {
                FileWriter writer = new FileWriter(file, false);
                writer.write(source);
                writer.flush();
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

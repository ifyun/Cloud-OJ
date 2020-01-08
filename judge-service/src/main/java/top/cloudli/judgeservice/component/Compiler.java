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
        File dir= new File(targetDir);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                log.info("目录 {} 不存在, 已创建.", targetDir);
            } else {
                log.info("无法创建目录.");
                System.exit(-1);
            }
        }
    }

    public Compile compile(int id, int languageId, String source) {
        log.info("正在编译, solutionId: {}", id);
        String src = writeCode(id, languageId, source);

        if (src.isEmpty())
            return new Compile(id, -1, "无法写入源码.");

        Language language = Language.get(languageId);

        if (language != null)
            switch (language) {
                case C_CPP:
                    return compileCpp(id, src);
                case JAVA:
                    // TODO 编译 Java 代码
                    break;

            }
        else
            return new Compile(id, -1, "不支持的语言");

        return new Compile(id, -1, "编译出错");
    }

    /**
     * 编译 C/C++
     *
     * @param id  solutionId
     * @param src 源文件
     * @return 包含编译信息的 Compile 对象
     */
    public Compile compileCpp(int id, String src) {
        // 构造编译命令
        processBuilder.command("g++", src, "-o", src.substring(0, src.indexOf(".")));
        try {
            Process process = processBuilder.start();
            // 获取错误流
            String error = getOutput(process.getErrorStream());

            if (error.isEmpty()) {
                log.info("编译完成: {}", id);
                return new Compile(id, 0, "file: " + src);
            }

            return new Compile(id, -1, error);
        } catch (IOException e) {
            log.error("编译异常: {}", e.getMessage());
            return new Compile(id, -1, e.getMessage());
        }
    }

    private String getOutput(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    /**
     * 将源码写入临时文件
     *
     * @param id       solutionId
     * @param language 语言 Id
     * @param source   源码
     * @return 文件路径
     */
    private String writeCode(int id, int language, String source) {
        File file = new File(targetDir + id + Language.getExt(language));

        try {
            if (file.exists() || file.createNewFile()) {
                FileWriter writer = new FileWriter(file, false);
                log.info(source);
                writer.write(source);
                writer.flush();
                return file.getPath();
            } else {
                log.error("无法创建文件 {}", file.getName());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        if (file.delete()) {
            log.info("已删除临时源代码文件.");
        }

        return "";
    }
}

package cloud.oj.core.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.io.File;

@Slf4j
@Getter
@ConfigurationProperties("app")
public class AppConfig {

    private final String fileDir;

    public AppConfig(ApplicationContext context, String fileDir) {
        var home = System.getProperty("user.home");

        if (fileDir == null) {
            this.fileDir = home + "/.local/cloud-oj/";
        } else if (!fileDir.endsWith("/")) {
            this.fileDir = fileDir + "/";
        } else {
            this.fileDir = fileDir;
        }

        var dir = new File(this.fileDir);

        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建目录失败: {}", fileDir);
            SpringApplication.exit(context, () -> -1);
        }

        log.info("数据文件目录: {}", this.fileDir);
    }
}

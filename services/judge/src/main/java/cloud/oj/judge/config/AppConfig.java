package cloud.oj.judge.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties("app")
public class AppConfig {
    @Resource
    private ApplicationContext applicationContext;

    private boolean fileSync = false;

    private boolean autoCleanSolution = true;

    private int judgePoolSize = 4;

    private String fileDir = "/var/lib/cloud-oj/";

    private String codeDir = "/tmp/code/";

    @PostConstruct
    public void init() {
        if (!fileDir.endsWith("/")) {
            fileDir += '/';
        }

        if (!codeDir.endsWith("/")) {
            codeDir += "/";
        }

        var cpuCores = Runtime.getRuntime().availableProcessors();

        if (judgePoolSize >= cpuCores) {
            judgePoolSize = cpuCores - 1;
        }

        if (judgePoolSize == 0) {
            judgePoolSize = 1;
        }

        log.info("Data dir: {}", fileDir);
        log.info("Code dir: {}", codeDir);
        log.info("Judge Pool Size: {}.", judgePoolSize);

        createDir(fileDir + "test_data");
        createDir(codeDir);
    }

    private void createDir(String path) {
        File dir = new File(path);

        if (!dir.exists() && !dir.mkdirs()) {
            log.error("Create {} failed.", path);
            SpringApplication.exit(applicationContext, () -> -1);
        }
    }
}

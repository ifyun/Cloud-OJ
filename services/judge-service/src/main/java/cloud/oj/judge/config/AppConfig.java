package cloud.oj.judge.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties("app")
public class AppConfig {
    private boolean fileSync = false;
    private boolean autoCleanSolution = true;
    private String fileDir = "/var/lib/cloud_oj/";
    private String codeDir = "/tmp/code/";
    private int judgePoolSize = 4;

    @PostConstruct
    private void init() {
        if (!fileDir.endsWith("/")) {
            fileDir += '/';
        }

        if (!codeDir.endsWith("/")) {
            codeDir += "/";
        }

        createDir(fileDir + "test_data");
        createDir(codeDir);

        var cpuCores = Runtime.getRuntime().availableProcessors();

        if (judgePoolSize >= cpuCores) {
            judgePoolSize = cpuCores - 1;
        }

        if (judgePoolSize == 0) {
            judgePoolSize = 1;
        }

        log.info("Judge Pool Size: {}.", judgePoolSize);
    }

    private void createDir(String path) {
        File dir = new File(path);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("Create {} failed.", path);
        }
    }
}

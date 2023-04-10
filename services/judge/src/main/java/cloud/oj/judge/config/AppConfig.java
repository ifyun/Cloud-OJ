package cloud.oj.judge.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties("app")
public class AppConfig {
    @Resource
    private ApplicationContext applicationContext;

    private boolean autoCleanSolution = true;

    private int judgePoolSize = 0;

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

        if (judgePoolSize >= cpuCores || judgePoolSize <= 0) {
            judgePoolSize = cpuCores - 1;
        }

        createDir(fileDir + "data");
        createDir(codeDir);

        log.info("测试数据目录: {}", fileDir);
        log.info("临时代码目录: {}", codeDir);
        log.info("JUDGE线程数: {}", judgePoolSize + 1);
        log.info("自动删除判题产物: {}", autoCleanSolution);
    }

    private void createDir(String path) {
        File dir = new File(path);

        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建目录失败: {}", path);
            SpringApplication.exit(applicationContext, () -> -1);
        }
    }
}

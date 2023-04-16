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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties("app")
public class AppConfig {
    @Resource
    private ApplicationContext applicationContext;

    private boolean autoCleanSolution = true;

    private List<Integer> judgeCpus = Collections.singletonList(0);

    private List<Integer> cpus;

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

        createDir(fileDir + "data");
        createDir(codeDir);
        configCpus();

        log.info("测试数据目录: {}", fileDir);
        log.info("临时代码目录: {}", codeDir);
        log.info("自动删除判题产物: {}", autoCleanSolution);
    }

    private void configCpus() {
        var availableCpus = Runtime.getRuntime().availableProcessors();

        // 只有一个元素，表示 CPU 数量
        if (judgeCpus.size() == 1) {
            var count = judgeCpus.get(0);
            cpus = new ArrayList<>(count);

            if (count >= availableCpus || count <= 0) {
                count = availableCpus;
            }

            for (int i = 0; i < count; i++) {
                cpus.add(i);
            }
        } else {
            // 有多个元素，每个对应一个 CPU
            var max = judgeCpus.stream().max(Comparator.comparing(Integer::intValue));
            var min = judgeCpus.stream().min(Comparator.comparing(Integer::intValue));

            if (max.isEmpty() || max.get() >= availableCpus) {
                log.error("Bad value: judge-cpus");
                SpringApplication.exit(applicationContext, () -> -1);
            }

            if (min.isEmpty() || min.get() < 0) {
                log.error("Bad value: judge-cpus");
                SpringApplication.exit(applicationContext, () -> -1);
            }

            cpus = judgeCpus;
        }
    }

    private void createDir(String path) {
        File dir = new File(path);

        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建目录失败: {}", path);
            SpringApplication.exit(applicationContext, () -> -1);
        }
    }
}

package cloud.oj.judge.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.*;

@Slf4j
@Getter
@Setter
@ConfigurationProperties("app")
public class AppConfig {
    private final ApplicationContext applicationContext;

    private final boolean autoCleanSolution;

    private final String fileDir;

    private final String codeDir;

    private final String judgeCpus;

    private List<Integer> cpus;

    public AppConfig(ApplicationContext context, Boolean autoCleanSolution, String fileDir, String codeDir, String judgeCpus) {
        var home = System.getProperty("user.home");
        this.applicationContext = context;
        this.autoCleanSolution = Optional.ofNullable(autoCleanSolution).orElse(true);
        this.judgeCpus = Optional.ofNullable(judgeCpus).orElse("4");

        if (fileDir == null) {
            this.fileDir = home + "/.local/cloud-oj/";
        } else if (!fileDir.endsWith("/")) {
            this.fileDir = fileDir + '/';
        } else {
            this.fileDir = fileDir;
        }

        if (codeDir == null) {
            this.codeDir = "/tmp/code/";
        } else if (!codeDir.endsWith("/")) {
            this.codeDir = codeDir + "/";
        } else {
            this.codeDir = codeDir;
        }

        createDir(this.fileDir + "data");
        createDir(this.codeDir);
        configCpus();

        log.info("测试数据目录: {}", fileDir);
        log.info("临时代码目录: {}", codeDir);
        log.info("自动删除判题产物: {}", autoCleanSolution);
    }

    private void configCpus() {
        var cpuList = Arrays.stream(judgeCpus.split(",")).map(Integer::valueOf).toList();
        var availableCpus = Runtime.getRuntime().availableProcessors();

        // 只有一个元素，表示 CPU 数量
        if (cpuList.size() == 1) {
            var count = cpuList.get(0);
            cpus = new ArrayList<>(count);

            if (count >= availableCpus || count <= 0) {
                count = availableCpus;
            }

            for (int i = 0; i < count; i++) {
                cpus.add(i);
            }
        } else {
            // 有多个元素，每个对应一个 CPU
            var max = cpuList.stream().max(Comparator.comparing(Integer::intValue));
            var min = cpuList.stream().min(Comparator.comparing(Integer::intValue));

            if (max.isEmpty() || max.get() >= availableCpus) {
                log.error("Bad value: judge-cpus");
                SpringApplication.exit(applicationContext, () -> -1);
            }

            if (min.isEmpty() || min.get() < 0) {
                log.error("Bad value: judge-cpus");
                SpringApplication.exit(applicationContext, () -> -1);
            }

            cpus = cpuList;
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

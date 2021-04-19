package group._204.oj.judge;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
public class JudgeServiceApp {
    /**
     * 提取判题程序到 /opt/bin
     */
    private static void extractRunner() {
        try {
            InputStream inputStream = new ClassPathResource("bin/judge-runner").getInputStream();
            File dir = new File("/opt/bin");

            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    log.error("Failed to mkdir: {}", dir.getAbsolutePath());
                    System.exit(1);
                }
            }

            File file = new File("/opt/bin/judge-runner");
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            if (!file.setExecutable(true)) {
                log.error("Set executable failed.");
                System.exit(1);
            }

            log.info("Extracted judge-runner to {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error(e.getMessage());
            System.exit(1);
        }
    }

    private static void checkSystem() throws IOException {
        if (!SystemUtils.IS_OS_LINUX) {
            log.error("Only support Linux.");
            System.exit(1);
        }

        String arch = System.getProperty("os.arch");
        log.info("OS Arch: {}", arch);

        if (!arch.equals("amd64")) {
            log.error("Only support x64.");
            System.exit(1);
        }

        Process p = Runtime.getRuntime().exec("id -u");
        String uid = StringUtils.chomp(IOUtils.toString(p.getInputStream()));

        if (!uid.equals("0")) {
            log.error("Root permission required.");
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        checkSystem();
        extractRunner();
        SpringApplication.run(JudgeServiceApp.class, args);
    }
}

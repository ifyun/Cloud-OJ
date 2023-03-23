package cloud.oj.judge;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableTransactionManagement
@EnableDiscoveryClient
@SpringBootApplication
public class JudgeApp {
    private static void checkSystem() {
        if (!SystemUtils.IS_OS_LINUX) {
            log.error("Only support Linux.");
            System.exit(1);
        }

        var arch = System.getProperty("os.arch");
        log.info("OS Arch: {}", arch);

        if (!arch.equals("amd64")) {
            log.error("Only support x64.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        checkSystem();
        SpringApplication.run(JudgeApp.class, args);
    }
}
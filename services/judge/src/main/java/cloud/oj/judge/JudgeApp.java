package cloud.oj.judge;

import cloud.oj.judge.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <h2>使用 root 权限调试</h2>
 *
 * <code>mvn spring-boot:run -pl judge</code>
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
@EnableTransactionManagement
public class JudgeApp {

    public static void main(String[] args) {
        SpringApplication.run(JudgeApp.class, args);
    }
}

package cloud.oj.judge;

import cloud.oj.judge.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <h3>使用 ROOT 权限运行</h3>
 *
 * <code>sudo mvn spring-boot:run</code>
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

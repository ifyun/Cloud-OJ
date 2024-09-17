package cloud.oj.judge;

import cloud.oj.judge.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 使用 ROOT 权限运行
 * <p>
 *   <code>sudo mvn spring-boot:run</code>
 * </p>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableConfigurationProperties(AppConfig.class)
public class JudgeApp {

    public static void main(String[] args) {
        SpringApplication.run(JudgeApp.class, args);
    }
}

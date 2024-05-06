package cloud.oj.judge;

import cloud.oj.judge.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <h2>使用 ROOT 权限运行</h2>
 *
 * <b><code>sudo mvn spring-boot:run</code></b>
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
@EnableTransactionManagement
public class JudgeApp {

    public static void main(String[] args) {
        SpringApplication.run(JudgeApp.class, args);
    }
}

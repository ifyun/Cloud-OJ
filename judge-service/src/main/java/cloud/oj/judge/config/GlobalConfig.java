package cloud.oj.judge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalConfig {

    @Bean
    public AppHealth appHealth() {
        return new AppHealth();
    }
}

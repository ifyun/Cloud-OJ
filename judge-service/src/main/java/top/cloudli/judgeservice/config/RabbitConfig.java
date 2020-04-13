package top.cloudli.judgeservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue judgeQueue() {
        return new Queue("JudgeQueue");
    }

    @Bean
    public Queue commitQueue() {
        return new Queue("CommitQueue");
    }
}

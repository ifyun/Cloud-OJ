package group._204.oj.judge.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String JUDGE_QUEUE = "JudgeQueue";
    public static final String COMMIT_QUEUE = "CommitQueue";

    @Bean
    public Queue judgeQueue() {
        return new Queue(JUDGE_QUEUE);
    }

    @Bean
    public Queue commitQueue() {
        return new Queue(COMMIT_QUEUE);
    }
}

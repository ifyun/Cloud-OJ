package group._204.oj.judge.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    public static final String JUDGE_QUEUE = "JudgeQueue";
    public static final String COMMIT_QUEUE = "CommitQueue";

    @Value("${commit-queue-length:4000}")
    private Integer commitQueueLength;

    @Bean
    public Queue commitQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-max-length", commitQueueLength);
        args.put("x-overflow", "reject-publish");
        return new Queue(COMMIT_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue judgeQueue() {
        return new Queue(JUDGE_QUEUE);
    }
}

package cloud.oj.judge.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String JUDGE_QUEUE = "JudgeQueue";
    public static final String COMMIT_QUEUE = "CommitQueue";

    @Bean
    public Queue commitQueue() {
        return new Queue(COMMIT_QUEUE);
    }

    @Bean
    public Queue judgeQueue() {
        return new Queue(JUDGE_QUEUE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

package cloud.oj.judge.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String SUBMIT_QUEUE = "SubmitQueue";

    public static final String JUDGE_QUEUE = "JudgeQueue";

    @Bean
    public Queue submitQueue() {
        return QueueBuilder.durable(SUBMIT_QUEUE)
                .overflow(QueueBuilder.Overflow.rejectPublish)
                .maxLength(5000)
                .build();
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

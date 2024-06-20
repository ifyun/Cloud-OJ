package cloud.oj.judge.config;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.PolicyInfo;
import com.rabbitmq.http.client.domain.QueueInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

    public static final String SUBMIT_QUEUE = "Submit";

    public static final String JUDGE_QUEUE = "Judge";

    private static final Map<String, Object> DEFAULT_POLICY = Map.of(
            "max-length", 5000,
            "overflow", "reject-publish"
    );

    private final RabbitProperties rabbitProperties;

    @Bean
    public Client rabbitClient() throws MalformedURLException, URISyntaxException {
        var url = new URL("http", rabbitProperties.getHost(), 15672, "/api");
        return new Client(url, rabbitProperties.getUsername(), rabbitProperties.getPassword());
    }

    @Bean
    public Queue submitQueue(Client rabbitClient) {
        rabbitClient.declareQueue("/", SUBMIT_QUEUE, new QueueInfo(true, false, false));

        if (!"limit".equals(rabbitClient.getQueue("/", RabbitConfig.SUBMIT_QUEUE).getPolicy())) {
            var policy = new PolicyInfo(RabbitConfig.SUBMIT_QUEUE, 1, "queues", DEFAULT_POLICY);
            rabbitClient.declarePolicy("/", "limit", policy);
        }

        return new Queue(SUBMIT_QUEUE);
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

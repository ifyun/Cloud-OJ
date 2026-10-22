package cloud.oj.judge.config;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.PolicyInfo;
import com.rabbitmq.http.client.domain.QueueInfo;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

    public static final String SUBMIT_QUEUE = "Submit";

    public static final String JUDGE_QUEUE = "Judge";

    private static final Map<String, Object> DEFAULT_POLICY = Map.of(
            "max-length", 3000,
            "overflow", "reject-publish"
    );

    private final RabbitProperties rabbitProperties;

    @Bean
    public Client rabbitClient() throws MalformedURLException, URISyntaxException {
        var url = new URI("http", null, rabbitProperties.getHost(), 15672, "/api", null, null).toURL();
        return new Client(url, rabbitProperties.getUsername(), rabbitProperties.getPassword());
    }

    @Bean
    public Queue submitQueue(Client rabbitClient) {
        var policy = new PolicyInfo(RabbitConfig.SUBMIT_QUEUE, 1, "queues", DEFAULT_POLICY);

        rabbitClient.declareQueue("/", SUBMIT_QUEUE, new QueueInfo(true, false, false));
        rabbitClient.declarePolicy("/", "limit", policy);

        return new Queue(SUBMIT_QUEUE);
    }

    @Bean
    public Queue judgeQueue() {
        return new Queue(JUDGE_QUEUE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean("submitListenerFactory")
    public SimpleRabbitListenerContainerFactory submitListenerFactory(HikariDataSource dataSource,
                                                                      CachingConnectionFactory connectionFactory) {
        var prefetch = dataSource.getMaximumPoolSize() / 2;
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(prefetch);
        factory.setPrefetchCount(prefetch);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean("judgeListenerFactory")
    public SimpleRabbitListenerContainerFactory judgeListenerFactory(AppConfig appConfig,
                                                                     CachingConnectionFactory connectionFactory) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(1);
        factory.setPrefetchCount(appConfig.getCpus().size());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}

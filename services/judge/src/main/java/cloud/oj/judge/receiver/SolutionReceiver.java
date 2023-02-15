package cloud.oj.judge.receiver;

import cloud.oj.judge.component.JudgementAsync;
import cloud.oj.judge.config.RabbitConfig;
import cloud.oj.judge.entity.CommitData;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.service.SubmitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 消息接收（提交和判题）
 */
@Slf4j
@Component
public class SolutionReceiver {

    private final SubmitService submitService;

    private final JudgementAsync judgementAsync;

    @Autowired
    public SolutionReceiver(SubmitService submitService, JudgementAsync judgementAsync) {
        this.submitService = submitService;
        this.judgementAsync = judgementAsync;
    }

    /**
     * 监听判题队列
     */
    @RabbitHandler
    @RabbitListener(queues = RabbitConfig.JUDGE_QUEUE)
    public void handleJudgement(@Payload Solution solution, @Headers Map<String, Object> headers, Channel channel) {
        judgementAsync.judge(solution, (Void) -> {
            try {
                channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }

    /**
     * 监听提交队列
     */
    @RabbitHandler
    @RabbitListener(queues = RabbitConfig.COMMIT_QUEUE)
    public void handleSubmission(@Payload CommitData data, @Headers Map<String, Object> headers, Channel channel) {
        submitService.submit(data);
        try {
            channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

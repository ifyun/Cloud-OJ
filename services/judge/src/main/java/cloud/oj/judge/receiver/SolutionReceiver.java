package cloud.oj.judge.receiver;

import cloud.oj.judge.component.JudgementEntry;
import cloud.oj.judge.config.RabbitConfig;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.entity.SubmitData;
import cloud.oj.judge.service.SubmitService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 消息接收(提交和判题)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SolutionReceiver {

    private final SubmitService submitService;

    private final JudgementEntry judgementEntry;

    /**
     * 监听判题队列
     */
    @RabbitListener(queues = RabbitConfig.JUDGE_QUEUE, ackMode = "MANUAL", concurrency = "1")
    public void handleJudgement(@Payload Solution solution, @Headers Map<String, Object> headers, Channel channel)
            throws IOException {
        judgementEntry.judge(solution, () -> channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false));
    }

    /**
     * 监听提交队列
     */
    @RabbitListener(queues = RabbitConfig.SUBMIT_QUEUE, ackMode = "MANUAL", concurrency = "2")
    public void handleSubmission(@Payload SubmitData data, @Headers Map<String, Object> headers, Channel channel)
            throws IOException {
        try {
            submitService.submit(data);
            channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false);
        } catch (Exception e) {
            // submit 失败，重新入队
            log.error("提交失败(已重新入队): {}", e.getMessage());
            channel.basicNack((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false, true);
        }
    }
}

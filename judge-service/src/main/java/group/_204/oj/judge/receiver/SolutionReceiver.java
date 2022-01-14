package group._204.oj.judge.receiver;

import com.rabbitmq.client.Channel;
import group._204.oj.judge.component.JudgementAsync;
import group._204.oj.judge.config.RabbitConfig;
import group._204.oj.judge.model.CommitData;
import group._204.oj.judge.model.Solution;
import group._204.oj.judge.service.SubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * 消息接收（提交和判题）
 */
@Slf4j
@Component
public class SolutionReceiver {

    @Resource
    private SubmitService submitService;

    @Resource
    private JudgementAsync judgementAsync;

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

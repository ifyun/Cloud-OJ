package group._204.oj.judge.task;

import com.rabbitmq.client.Channel;
import group._204.oj.judge.config.RabbitConfig;
import group._204.oj.judge.model.CommitData;
import group._204.oj.judge.service.CommitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import group._204.oj.judge.component.JudgementAsync;
import group._204.oj.judge.model.Solution;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * 消息接收
 */
@Slf4j
@Component
public class TaskReceiver {

    @Resource
    private CommitService commitService;

    @Resource
    private JudgementAsync judgementAsync;

    @RabbitHandler
    @RabbitListener(queues = RabbitConfig.JUDGE_QUEUE)
    public void receiveTask(@Payload Solution solution, @Headers Map<String, Object> headers, Channel channel) {
        judgementAsync.judge(solution, (Void) -> {
            try {
                channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG), true);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }

    @RabbitHandler
    @RabbitListener(queues = RabbitConfig.COMMIT_QUEUE)
    public void receiveCommit(@Payload CommitData data, @Headers Map<String, Object> headers, Channel channel) {
        commitService.commit(data);
        try {
            channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG), true);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

package cloud.oj.judge.controller;

import cloud.oj.judge.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class QueueInfoController {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 获取消息队列信息
     */
    @GetMapping("queue_info")
    public ResponseEntity<?> queueInfo() {
        var q1 = getQueueInfo(RabbitConfig.SUBMIT_QUEUE);
        var q2 = getQueueInfo(RabbitConfig.JUDGE_QUEUE);

        return ResponseEntity.ok(Arrays.asList(q1, q2));
    }

    /**
     * 获取队列信息
     *
     * @param queueName 队列名称
     * @return {@link QueueInformation} 队列信息
     */
    private QueueInformation getQueueInfo(String queueName) {
        var declareOk = rabbitTemplate.execute(channel -> channel.queueDeclarePassive(queueName));
        return declareOk == null ?
                null :
                new QueueInformation(declareOk.getQueue(), declareOk.getMessageCount(), declareOk.getConsumerCount());
    }
}

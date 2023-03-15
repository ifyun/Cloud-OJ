package cloud.oj.judge.controller;

import cloud.oj.judge.config.RabbitConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("admin")
public class QueueInfoController {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public QueueInfoController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class QueueInfo {
        private int submit = 0;
        private int judge = 0;
    }

    /**
     * 获取消息队列信息
     */
    @GetMapping("queue_info")
    public ResponseEntity<?> getQueueInfo() {
        var queueInfo = new QueueInfo(
                getMessageCount(RabbitConfig.SUBMIT_QUEUE),
                getMessageCount(RabbitConfig.JUDGE_QUEUE)
        );

        return ResponseEntity.ok(queueInfo);
    }

    /**
     * 获取队列中消息的数量
     *
     * @param queueName 队列名称
     * @return 消息数量
     */
    private int getMessageCount(String queueName) {
        var declareOk =
                rabbitTemplate.execute(channel -> channel.queueDeclarePassive(queueName));
        return declareOk == null ? 0 : declareOk.getMessageCount();
    }
}

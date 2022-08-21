package group._204.oj.judge.controller;

import group._204.oj.judge.config.RabbitConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("admin/queue_info")
public class QueueInfoController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class QueueInfo {
        private int inCommitQueue = 0;
        private int inJudgeQueue = 0;
    }

    /**
     * 获取消息队列信息
     */
    @GetMapping()
    public ResponseEntity<?> getQueueInfo() {
        var queueInfo = new QueueInfo(
                getMessageCount(RabbitConfig.COMMIT_QUEUE),
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

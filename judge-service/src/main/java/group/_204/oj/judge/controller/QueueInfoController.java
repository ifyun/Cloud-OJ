package group._204.oj.judge.controller;

import com.rabbitmq.client.AMQP;
import group._204.oj.judge.config.RabbitConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@RequestMapping("pro/queue_info")
@Api(tags = "队列信息")
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

    @ApiOperation(value = "获取消息队列信息", notes = "返回 RabbitMQ 队列中消息的数量")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = QueueInfo.class)
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getQueueInfo() {
        QueueInfo queueInfo = new QueueInfo(
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
        AMQP.Queue.DeclareOk declareOk =
                rabbitTemplate.execute(channel -> channel.queueDeclarePassive(queueName));
        return declareOk == null ? 0 : declareOk.getMessageCount();
    }
}

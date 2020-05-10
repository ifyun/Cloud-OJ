package top.cloudli.judgeservice.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.component.JudgementAsync;
import top.cloudli.judgeservice.model.CommitData;
import top.cloudli.judgeservice.model.Solution;
import top.cloudli.judgeservice.service.CommitService;

import javax.annotation.Resource;

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
    @RabbitListener(queues = "JudgeQueue")
    public void receiveTask(Solution solution) {
        judgementAsync.judge(solution);
    }

    @RabbitHandler
    @RabbitListener(queues = "CommitQueue")
    public void receiveCommit(CommitData data) {
        commitService.commit(data);
    }
}

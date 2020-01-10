package top.cloudli.judgeservice.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.component.JudgementAsync;
import top.cloudli.judgeservice.model.Language;
import top.cloudli.judgeservice.model.Solution;

import javax.annotation.Resource;

/**
 * 判题任务，从 RabbitMQ 获取 Solution
 */
@Slf4j
@Component
public class TaskReceiver {

    @Resource
    private JudgementAsync judgementAsync;

    @RabbitHandler
    @RabbitListener(queues = "JudgeQueue")
    public void receiveTask(Solution solution) {
        log.info("Take solutionId: {} from JudgeQueue", solution.getSolutionId());
        judgementAsync.judge(solution);
    }
}

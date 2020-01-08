package top.cloudli.judgeservice.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.component.CompilerAsync;
import top.cloudli.judgeservice.dao.SolutionDao;
import top.cloudli.judgeservice.model.Solution;

import javax.annotation.Resource;

/**
 * 判题任务，从 RabbitMQ 获取 Solution
 */
@Slf4j
@Component
@RabbitListener(queues = "JudgeQueue")
public class TaskReceiver {

    @Resource
    private CompilerAsync compilerAsync;

    @Resource
    private SolutionDao solutionDao;

    @RabbitHandler
    public void receiveTask(Solution solution) {
        // TODO 判题
    }

    public void judge() {

    }
}

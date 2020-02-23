package top.cloudli.judgeservice.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.dao.SolutionDao;
import top.cloudli.judgeservice.dao.SourceCodeDao;
import top.cloudli.judgeservice.model.Solution;
import top.cloudli.judgeservice.model.SolutionState;
import top.cloudli.judgeservice.model.SourceCode;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class TaskSender {

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private SourceCodeDao sourceCodeDao;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    private Queue queue;

    /**
     * 发送已提交的 solution 到消息队列
     */
    @Scheduled(fixedDelay = 500, initialDelay = 1000)
    public void sendCommitted() {
        List<Solution> solutions = solutionDao.getSubmitted();

        for (Solution solution : solutions) {
            solution.setSourceCode(sourceCodeDao.get(solution.getSolutionId()).getCode());
            rabbitTemplate.convertAndSend(queue.getName(), solution);

            log.info("solution {} in JudgeQueue.", solution.getSolutionId());

            solution.setState(SolutionState.IN_JUDGE_QUEUE.ordinal());
            solutionDao.update(solution);
        }
    }
}

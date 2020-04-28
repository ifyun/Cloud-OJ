package top.cloudli.judgeservice.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.dao.SolutionDao;
import top.cloudli.judgeservice.dao.SourceCodeDao;
import top.cloudli.judgeservice.dao.TaskDao;
import top.cloudli.judgeservice.model.Solution;
import top.cloudli.judgeservice.model.SolutionState;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class TaskSender {

    private final String uuid = UUID.randomUUID().toString();

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private SourceCodeDao sourceCodeDao;

    @Resource
    private TaskDao taskDao;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Queue judgeQueue;

    @PostConstruct
    public void init() throws InterruptedException {
        Thread.sleep(10000);
        // 设置要执行任务的 UUID，保证不重复执行
        taskDao.setUUID("send_committed", uuid);
        log.info("SEND_COMMITTED_TASK_UUID: {}", uuid);
    }

    /**
     * 发送已提交的 solution 到消息队列
     */
    @Scheduled(fixedDelay = 1000, initialDelay = 3000)
    public void sendCommitted() {
        // 判断是否和数据库中的 UUID 相同，不相同什么也不做
        if (taskDao.getUUID("send_committed").equals(uuid)) {
            List<Solution> solutions = solutionDao.getSubmitted();

            for (Solution solution : solutions) {
                solution.setSourceCode(sourceCodeDao.get(solution.getSolutionId()).getCode());
                rabbitTemplate.convertAndSend(judgeQueue.getName(), solution);
                log.info("solution {} in JudgeQueue.", solution.getSolutionId());
                solution.setState(SolutionState.IN_JUDGE_QUEUE.ordinal());
                solutionDao.update(solution);
            }
        }
    }
}

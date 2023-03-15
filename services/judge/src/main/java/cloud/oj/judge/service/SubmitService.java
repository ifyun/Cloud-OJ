package cloud.oj.judge.service;

import cloud.oj.judge.component.SolutionState;
import cloud.oj.judge.dao.SolutionDao;
import cloud.oj.judge.dao.SourceCodeDao;
import cloud.oj.judge.entity.CommitData;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.entity.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SubmitService {

    private final SolutionDao solutionDao;

    private final SourceCodeDao sourceCodeDao;

    private final RabbitTemplate rabbitTemplate;

    private final Queue judgeQueue;

    @Autowired
    public SubmitService(SolutionDao solutionDao, SourceCodeDao sourceCodeDao, RabbitTemplate rabbitTemplate, Queue judgeQueue) {
        this.solutionDao = solutionDao;
        this.sourceCodeDao = sourceCodeDao;
        this.rabbitTemplate = rabbitTemplate;
        this.judgeQueue = judgeQueue;
    }

    /**
     * 保存提交到数据库
     * <p>隔离级别：读提交</p>
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void submit(CommitData commitData) throws AmqpException {
        var solution = new Solution(
                commitData.getSolutionId(),
                commitData.getUserId(),
                commitData.getProblemId(),
                commitData.getContestId(),
                commitData.getLanguage(),
                commitData.getSubmitTime()
        );

        var sourceCode = new SourceCode(solution.getSolutionId(), commitData.getSourceCode());

        solutionDao.add(solution);
        sourceCodeDao.add(sourceCode);

        solution.setSourceCode(commitData.getSourceCode());
        solution.setState(SolutionState.IN_JUDGE_QUEUE);
        solutionDao.updateState(solution);

        rabbitTemplate.convertAndSend(judgeQueue.getName(), solution);
    }
}

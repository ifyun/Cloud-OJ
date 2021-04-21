package group._204.oj.judge.service;

import group._204.oj.judge.model.*;
import group._204.oj.judge.type.SolutionState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.dao.SourceCodeDao;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
public class CommitService {

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private SourceCodeDao sourceCodeDao;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Queue judgeQueue;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void commit(CommitData commitData) {
        Solution solution = new Solution(
                commitData.getSolutionId(),
                commitData.getUserId(),
                commitData.getProblemId(),
                commitData.getContestId(),
                commitData.getLanguage(),
                commitData.getType()
        );

        SourceCode sourceCode = new SourceCode(solution.getSolutionId(), commitData.getSourceCode());

        solutionDao.add(solution);
        sourceCodeDao.add(sourceCode);

        solution.setSourceCode(commitData.getSourceCode());
        rabbitTemplate.convertAndSend(judgeQueue.getName(), solution);

        solution.setState(SolutionState.IN_JUDGE_QUEUE);
        solutionDao.update(solution);
    }
}

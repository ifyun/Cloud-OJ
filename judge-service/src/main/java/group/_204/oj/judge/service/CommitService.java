package group._204.oj.judge.service;

import group._204.oj.judge.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.dao.SourceCodeDao;

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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void commit(CommitData commitData) {
        Solution solution = new Solution(
                commitData.getSolutionId(),
                commitData.getUserId(),
                commitData.getProblemId(),
                commitData.getContestId(),
                commitData.getLanguage()
        );

        solutionDao.add(solution);

        String solutionId = solution.getSolutionId();
        SourceCode sourceCode = new SourceCode(solutionId, commitData.getSourceCode());

        sourceCodeDao.add(sourceCode);

        solution.setSourceCode(commitData.getSourceCode());
        rabbitTemplate.convertAndSend(judgeQueue.getName(), solution);

        solution.setState(SolutionState.IN_JUDGE_QUEUE.ordinal());
        solutionDao.update(solution);

        log.debug("加入判题队列: solutionId={}.", solution.getSolutionId());
    }

    public JudgeResult getResult(String solutionId) {
        return solutionDao.getResultBySolutionId(solutionId);
    }
}

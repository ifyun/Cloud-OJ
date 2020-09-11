package group._204.oj.judge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.dao.SourceCodeDao;
import group._204.oj.judge.model.CommitData;
import group._204.oj.judge.model.JudgeResult;
import group._204.oj.judge.model.Solution;
import group._204.oj.judge.model.SourceCode;

import javax.annotation.Resource;

@Slf4j
@Service
public class CommitService {

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private SourceCodeDao sourceCodeDao;

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
    }

    public JudgeResult getResult(String solutionId) {
        return solutionDao.getResultBySolutionId(solutionId);
    }
}

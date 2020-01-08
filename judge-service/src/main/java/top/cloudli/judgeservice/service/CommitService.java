package top.cloudli.judgeservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.cloudli.judgeservice.dao.SolutionDao;
import top.cloudli.judgeservice.dao.SourceCodeDao;
import top.cloudli.judgeservice.data.CommitData;
import top.cloudli.judgeservice.model.Solution;
import top.cloudli.judgeservice.model.SourceCode;

import javax.annotation.Resource;

@Slf4j
@Service
public class CommitService {

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private SourceCodeDao sourceCodeDao;

    public int commit(CommitData commitData) {
        Solution solution = new Solution(
                commitData.getUserId(),
                commitData.getProblemId(),
                commitData.getLanguage()
        );

        solutionDao.add(solution);
        int solutionId = solution.getSolutionId();
        SourceCode sourceCode = new SourceCode(solutionId, commitData.getSourceCode());
        log.info("提交 solution, solution_id = {}", solutionId);

        sourceCodeDao.add(sourceCode);
        return solutionId;
    }

    public Object getResult(int solutionId) {
        return solutionDao.getJudged();
    }
}

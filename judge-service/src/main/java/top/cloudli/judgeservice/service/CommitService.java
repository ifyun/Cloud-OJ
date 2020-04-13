package top.cloudli.judgeservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.cloudli.judgeservice.dao.SolutionDao;
import top.cloudli.judgeservice.dao.SourceCodeDao;
import top.cloudli.judgeservice.data.CommitData;
import top.cloudli.judgeservice.model.JudgeResult;
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

    public String commit(CommitData commitData) {
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
        log.info("提交 solution, solution_id = {}", solutionId);

        sourceCodeDao.add(sourceCode);
        return solutionId;
    }

    public JudgeResult getResult(String solutionId) {
        return solutionDao.getResultBySolutionId(solutionId);
    }
}

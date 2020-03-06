package top.cloudli.judgeservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.cloudli.judgeservice.dao.SolutionDao;
import top.cloudli.judgeservice.dao.SourceCodeDao;
import top.cloudli.judgeservice.data.CommitData;
import top.cloudli.judgeservice.model.JudgeResult;
import top.cloudli.judgeservice.model.Solution;
import top.cloudli.judgeservice.model.SourceCode;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CommitService {

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private SourceCodeDao sourceCodeDao;

    @Async
    public CompletableFuture<JudgeResult> commit(CommitData commitData) {
        Solution solution = new Solution(
                commitData.getUserId(),
                commitData.getProblemId(),
                commitData.getContestId(),
                commitData.getLanguage()
        );

        solutionDao.add(solution);
        int solutionId = solution.getSolutionId();
        SourceCode sourceCode = new SourceCode(solutionId, commitData.getSourceCode());
        log.info("提交 solution, solution_id = {}", solutionId);

        sourceCodeDao.add(sourceCode);
        return CompletableFuture.completedFuture(getResult(solutionId));
    }

    private JudgeResult getResult(int solutionId) {
        int retry = 8;
        JudgeResult judgeResult =  solutionDao.getJudgedBySolutionId(solutionId);

        while (judgeResult == null && retry-- > 0) {
            try {
                Thread.sleep(1000);
                judgeResult = solutionDao.getJudgedBySolutionId(solutionId);
            } catch (InterruptedException ignored) {
            }
        }

        return judgeResult;
    }
}

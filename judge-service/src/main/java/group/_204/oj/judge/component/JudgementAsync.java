package group._204.oj.judge.component;

import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.model.Compile;
import group._204.oj.judge.model.Solution;
import group._204.oj.judge.type.SolutionResult;
import group._204.oj.judge.type.SolutionState;
import group._204.oj.judge.task.FileCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class JudgementAsync {
    @Resource
    private CompilerAsync compilerAsync;

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private Judgement judgement;

    @Resource
    private FileCleaner fileCleaner;

    /**
     * 异步判题
     *
     * @param solution {@link Solution} 答案对象
     */
    @Async("ojExecutor")
    public void judge(Solution solution) {
        CompletableFuture<Compile> future = compilerAsync.compile(solution);
        try {
            Compile compile = future.get();
            if (compile.getState() == -1) {
                solution.setResult(SolutionResult.COMPILE_ERROR);
            } else {
                judgement.judge(solution);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("判题异常: {}", e.getMessage());
            solution.setResult(SolutionResult.JUDGE_ERROR);
        } finally {
            solution.setState(SolutionState.JUDGED);
        }

        fileCleaner.deleteTempFile(solution.getSolutionId(), solution.getLanguage());

        if (solutionDao.update(solution) != 0)
            log.info("判题完成: solutionId={}.", solution.getSolutionId());
    }
}

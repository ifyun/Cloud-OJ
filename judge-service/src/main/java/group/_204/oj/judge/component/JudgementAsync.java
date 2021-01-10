package group._204.oj.judge.component;

import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.model.Solution;
import group._204.oj.judge.type.SolutionResult;
import group._204.oj.judge.type.SolutionState;
import group._204.oj.judge.task.FileCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
     * @param solution {@link Solution}
     */
    @Async("judgeExecutor")
    public void judge(Solution solution) {
        compilerAsync.compile(solution, (compile -> {
            if (compile.getState() == -1) {
                solution.setResult(SolutionResult.CE);
            } else {
                judgement.judge(solution);
            }

            solution.setState(SolutionState.JUDGED);

            fileCleaner.deleteTempFile(solution.getSolutionId(), solution.getLanguage());

            if (solutionDao.update(solution) != 0) {
                log.debug("判题完成: solutionId={}.", solution.getSolutionId());
            }
        }));
    }
}

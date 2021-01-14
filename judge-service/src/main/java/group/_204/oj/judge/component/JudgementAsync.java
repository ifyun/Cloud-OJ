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
import java.util.function.Consumer;

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
     * @param callback 判题结束回调
     */
    @Async("judgeExecutor")
    public void judge(Solution solution, Consumer<Void> callback) {
        compilerAsync.compile(solution, (compile -> {
            if (compile.getState() == -1) {
                solution.setResult(SolutionResult.CE);
            } else {
                judgement.judge(solution);
            }

            solution.setState(SolutionState.JUDGED);
            solutionDao.update(solution);
            log.info("Judged: solutionId=[{}], user=[{}].", solution.getSolutionId(), solution.getUserId());
            callback.accept(null);
            fileCleaner.deleteTempFile(solution.getSolutionId(), solution.getLanguage());
        }));
    }
}

package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.dao.SolutionDao;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.utils.FileCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JudgementAsync {

    private final AppConfig appConfig;

    private final Judgement judgement;

    private final SolutionDao solutionDao;

    private final FileCleaner fileCleaner;

    @Autowired
    public JudgementAsync(AppConfig appConfig, Judgement judgement, SolutionDao solutionDao, FileCleaner fileCleaner) {
        this.appConfig = appConfig;
        this.judgement = judgement;
        this.solutionDao = solutionDao;
        this.fileCleaner = fileCleaner;
    }

    /**
     * 异步判题
     *
     * @param solution {@link Solution}
     * @param callback 判题结束回调
     */
    @Async("judgeExecutor")
    public void judge(Solution solution, Runnable callback) {
        try {
            judgement.judge(solution);
            log.debug("Judged: solution={}", solution.getSolutionId());
        } catch (Exception e) {
            // 判题发生异常，将结果设置为内部错误
            log.error(e.getMessage());
            solution.setState(SolutionState.JUDGED);
            solution.setResult(SolutionResult.IE);
            solutionDao.updateState(solution);
        } finally {
            callback.run();

            if (appConfig.isAutoCleanSolution()) {
                fileCleaner.deleteTempFile(solution.getSolutionId());
            }
        }
    }
}

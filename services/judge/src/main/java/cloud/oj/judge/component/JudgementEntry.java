package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.dao.SolutionDao;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.utils.FileCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JudgementEntry {

    private final AppConfig appConfig;

    private final Judgement judgement;

    private final FileCleaner fileCleaner;

    private final SolutionDao solutionDao;

    public JudgementEntry(AppConfig appConfig, Judgement judgement, FileCleaner fileCleaner, SolutionDao solutionDao) {
        this.appConfig = appConfig;
        this.judgement = judgement;
        this.fileCleaner = fileCleaner;
        this.solutionDao = solutionDao;
    }

    /**
     * 判题入口
     */
    @Async("judgeExecutor")
    public void judge(Solution solution) {
        try {
            judgement.judge(solution);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                e.printStackTrace();
            } else {
                log.error(e.getMessage());
            }
            // 判题发生异常，将结果设置为内部错误
            solution.setState(SolutionState.JUDGED);
            solution.setResult(SolutionResult.IE);
            solutionDao.updateState(solution);
        } finally {
            if (appConfig.isAutoCleanSolution()) {
                fileCleaner.deleteTempFile(solution.getSolutionId());
            }
        }
    }
}

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
public class JudgementEntry {

    private final AppConfig appConfig;

    private final Judgement judgement;

    private final SolutionDao solutionDao;

    private final FileCleaner fileCleaner;

    @Autowired
    public JudgementEntry(AppConfig appConfig, Judgement judgement, SolutionDao solutionDao, FileCleaner fileCleaner) {
        this.appConfig = appConfig;
        this.judgement = judgement;
        this.solutionDao = solutionDao;
        this.fileCleaner = fileCleaner;
    }

    /**
     * 判题入口
     */
    @Async("judgeExecutor")
    public void judge(Solution solution) {
        try {
            judgement.judge(solution);
        } catch (Exception e) {
            // 判题发生异常，将结果设置为内部错误
            log.error(e.getMessage());
            solution.setState(SolutionState.JUDGED);
            solution.setResult(SolutionResult.IE);
            solutionDao.updateState(solution);
        } finally {
            if (appConfig.isAutoCleanSolution()) {
                fileCleaner.deleteTempFile(solution.getSolutionId());
            }
        }
    }

    /**
     * 只有一个线程时，由调用线程执行判题
     */
    public void judgeSync(Solution solution) {
        judge(solution);
    }
}

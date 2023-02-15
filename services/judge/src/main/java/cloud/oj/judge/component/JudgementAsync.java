package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.dao.SolutionDao;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.enums.SolutionResult;
import cloud.oj.judge.enums.SolutionState;
import cloud.oj.judge.utils.FileCleaner;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

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

    @PostConstruct
    void init() {
        log.info("AutoCleanSolution: {}", appConfig.isAutoCleanSolution());
    }

    /**
     * 异步判题
     *
     * @param solution {@link Solution}
     * @param callback 判题结束回调
     */
    @Async("judgeExecutor")
    public void judge(Solution solution, Consumer<Void> callback) {
        try {
            judgement.judge(solution);
            log.info("Judged: solution({}), user({}).", solution.getSolutionId(), solution.getUserId());
        } catch (Exception e) {
            // 判题发生异常，将结果设置为内部错误
            log.error(e.getMessage());
            solution.setState(SolutionState.JUDGED);
            solution.setResult(SolutionResult.IE);
            solutionDao.updateState(solution);
        } finally {
            callback.accept(null);

            if (appConfig.isAutoCleanSolution()) {
                fileCleaner.deleteTempFile(solution.getSolutionId());
            }
        }
    }
}

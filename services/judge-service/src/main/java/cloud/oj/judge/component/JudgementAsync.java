package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.dao.SolutionDao;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.enums.SolutionResult;
import cloud.oj.judge.enums.SolutionState;
import cloud.oj.judge.utils.FileCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.function.Consumer;

@Slf4j
@Component
public class JudgementAsync {

    @Resource
    private AppConfig appConfig;

    @Resource
    private Judgement judgement;

    @Resource
    private SqlJudgement sqlJudgement;

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private FileCleaner fileCleaner;

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
            if (solution.getType() == 0) {
                judgement.judge(solution);
            } else {
                sqlJudgement.judge(solution);
            }
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

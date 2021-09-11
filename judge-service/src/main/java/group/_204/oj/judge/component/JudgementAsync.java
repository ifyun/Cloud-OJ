package group._204.oj.judge.component;

import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.model.Solution;
import group._204.oj.judge.type.SolutionState;
import group._204.oj.judge.utils.FileCleaner;
import group._204.oj.judge.type.SolutionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.function.Consumer;

@Slf4j
@Component
public class JudgementAsync {

    @Value("${project.auto-clean-solution:true}")
    private Boolean autoCleanSolution;

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
        log.info("AutoCleanSolution: {}", autoCleanSolution);
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
            solutionDao.update(solution);
        } finally {
            callback.accept(null);

            if (autoCleanSolution) {
                fileCleaner.deleteTempFile(solution.getSolutionId());
            }
        }
    }
}

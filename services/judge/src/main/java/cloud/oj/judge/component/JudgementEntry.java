package cloud.oj.judge.component;

import cloud.oj.judge.dao.SettingsDao;
import cloud.oj.judge.dao.SolutionDao;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.utils.FileCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static cloud.oj.judge.constant.Result.IE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JudgementEntry {

    private final SettingsDao settingsDao;

    private final SolutionDao solutionDao;

    private final Judgement judgement;

    private final FileCleaner fileCleaner;

    /**
     * 判题入口
     */
    @Async("judgeExecutor")
    public void judge(Solution solution) {
        try {
            judgement.judge(solution);
        } catch (Exception e) {
            var msg = ExceptionUtils.getRootCause(e).getMessage();
            log.error(msg);
            // 判题发生异常，将结果设置为内部错误
            solution.endWithError(IE, msg);
            solutionDao.updateWithResult(solution);
        } finally {
            if (settingsDao.isAutoDelSolutions()) {
                fileCleaner.deleteTempFile(solution.getSolutionId());
            }
        }
    }
}

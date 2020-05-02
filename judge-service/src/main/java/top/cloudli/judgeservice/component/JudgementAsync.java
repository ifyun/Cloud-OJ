package top.cloudli.judgeservice.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.dao.SolutionDao;
import top.cloudli.judgeservice.model.Compile;
import top.cloudli.judgeservice.model.Solution;
import top.cloudli.judgeservice.model.SolutionResult;
import top.cloudli.judgeservice.model.SolutionState;
import top.cloudli.judgeservice.task.FileCleaner;

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
                solution.setResult(SolutionResult.COMPILE_ERROR.ordinal());
            } else {
                judgement.judge(solution);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("判题出现异常: {}", e.getMessage());
            solution.setResult(SolutionResult.WRONG.ordinal());
        } finally {
            solution.setState(SolutionState.JUDGED.ordinal());
        }

        // 删除临时文件
        fileCleaner.deleteTempFile(solution.getSolutionId(),
                solution.getLanguage(),
                Judgement.isWindows);

        if (solutionDao.update(solution) != 0)
            log.info("solutionId: {} 判题完成.", solution.getSolutionId());
    }
}

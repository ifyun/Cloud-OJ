package top.cloudli.judgeservice.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.dao.SolutionDao;
import top.cloudli.judgeservice.model.Compile;
import top.cloudli.judgeservice.model.Solution;

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

    @Async("ojExecutor")
    public void judge(Solution solution) {
        CompletableFuture<Compile> future = compilerAsync.compile(solution);
        try {
            Compile compile = future.get();
            solution.setState(0);
            if (compile.getState() == -1) {
                solution.setResult(3);
            } else {
                judgement.judge(solution);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
            solution.setResult(3);
        }

        if (solutionDao.update(solution) != 0)
            log.info("判题完成, solutionId: {}", solution.getSolutionId());
    }
}

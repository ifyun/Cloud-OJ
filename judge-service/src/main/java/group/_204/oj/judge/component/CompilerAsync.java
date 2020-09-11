package group._204.oj.judge.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import group._204.oj.judge.dao.CompileDao;
import group._204.oj.judge.model.Compile;
import group._204.oj.judge.model.Solution;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class CompilerAsync {

    @Resource
    private CompileDao compileDao;

    @Resource
    private Compiler compiler;

    /**
     * 异步编译
     *
     * @param solution 答案对象
     * @return {@link Compile} 编译结果
     */
    @Async("ojExecutor")
    public CompletableFuture<Compile> compile(Solution solution) {
        Compile compile = compiler.compile(
                solution.getSolutionId(),
                solution.getLanguage(),
                solution.getSourceCode());
        compileDao.add(compile);

        return CompletableFuture.completedFuture(compile);
    }
}

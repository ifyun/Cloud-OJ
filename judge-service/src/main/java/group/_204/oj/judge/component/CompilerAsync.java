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
     * @param solution {@link Solution}
     * @return {@link Compile} 编译结果
     */
    @Async("judgeExecutor")
    public CompletableFuture<Compile> compile(Solution solution) {
        Compile compile = compiler.compile(solution);
        compileDao.add(compile);

        return CompletableFuture.completedFuture(compile);
    }
}

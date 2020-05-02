package top.cloudli.judgeservice.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.dao.CompileDao;
import top.cloudli.judgeservice.model.Compile;
import top.cloudli.judgeservice.model.Solution;

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

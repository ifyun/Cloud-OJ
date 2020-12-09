package group._204.oj.judge.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import group._204.oj.judge.dao.CompileDao;
import group._204.oj.judge.model.Compile;
import group._204.oj.judge.model.Solution;

import javax.annotation.Resource;
import java.util.function.Consumer;

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
     * @param callback 回调
     */
    @Async("judgeExecutor")
    public void compile(Solution solution, Consumer<Compile> callback) {
        Compile compile = compiler.compile(solution);
        compileDao.add(compile);
        callback.accept(compile);
    }
}

package top.cloudli.judgeservice.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.dao.CompileDao;
import top.cloudli.judgeservice.dao.SourceCodeDao;
import top.cloudli.judgeservice.model.Compile;
import top.cloudli.judgeservice.model.Solution;
import top.cloudli.judgeservice.model.SourceCode;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class CompilerAsync {

    @Resource
    private SourceCodeDao sourceCodeDao;

    @Resource
    private CompileDao compileDao;

    @Resource
    private Compiler compiler;

    @Async("ojExecutor")
    public CompletableFuture<Compile> compile(Solution solution) {
        int solutionId = solution.getSolutionId();
        int languageId = solution.getLanguage();

        SourceCode sourceCode = sourceCodeDao.get(solutionId);

        Compile compile = compiler.compile(solutionId, languageId, sourceCode.getCode());
        compileDao.add(compile);

        return CompletableFuture.completedFuture(compile);
    }
}

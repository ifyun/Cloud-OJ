package top.cloudli.judgeservice.task;

import org.springframework.beans.factory.annotation.Value;
import top.cloudli.judgeservice.model.Solution;
import top.cloudli.judgeservice.model.SourceCode;

/**
 * 判题任务
 */
public class JudgeTask {

    @Value("${project.target-dir}")
    private String targetDir;

    private void compile(Solution solution, SourceCode sourceCode) {

    }
}

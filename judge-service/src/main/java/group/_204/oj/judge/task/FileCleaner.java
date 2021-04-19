package group._204.oj.judge.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * 文件清理，删除判题产生的临时代码文件
 */
@Slf4j
@Component
public class FileCleaner {

    @Value("${project.code-dir}")
    private String codeDir;

    @PostConstruct
    public void init() {
        if (!codeDir.endsWith("/")) {
            codeDir += '/';
        }
        FileUtils.deleteQuietly(new File(codeDir));
    }

    @Async
    public void deleteTempFile(String solutionId) {
        try {
            FileUtils.deleteDirectory(new File(codeDir + solutionId));
        } catch (IOException e) {
            log.warn("Delete solution dir failed: solutionId={}", solutionId);
        }
    }
}

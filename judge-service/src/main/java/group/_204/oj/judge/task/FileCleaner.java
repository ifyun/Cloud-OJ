package group._204.oj.judge.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

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
        File dir = new File(codeDir);
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                delete(file);
            }
        }
    }

    @Async
    public void deleteTempFile(String solutionId) {
        delete(new File(codeDir + solutionId));
    }

    private void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                for (File f : files) {
                    delete(f);
                }
            }
        }
        if (!file.delete()) {
            log.warn("Delete failed: path={}", file.getAbsolutePath());
        }
    }
}

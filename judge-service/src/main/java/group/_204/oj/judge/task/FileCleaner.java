package group._204.oj.judge.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import group._204.oj.judge.type.Language;

import javax.annotation.PostConstruct;
import java.io.File;

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
    public void deleteTempFile(String solutionId, int languageId) {
        Language language = Language.get(languageId);

        if (language == null) {
            return;
        }

        delete(new File(codeDir + solutionId));
    }

    private void delete(File file) {
        boolean emptyDir = false;
        File[] files = file.listFiles();

        if (file.isFile() && !file.delete()) {
            log.warn("Delete file failed: path={}", file.getAbsolutePath());
        } else if (files != null) {
            for (File f : files) {
                emptyDir = f.delete();
            }
        }

        if (emptyDir && !file.delete()) {
            log.warn("Delete directory failed: path={}", file.getAbsolutePath());
        }
    }
}

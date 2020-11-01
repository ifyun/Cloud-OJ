package group._204.oj.judge.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import group._204.oj.judge.model.Language;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Objects;

@Slf4j
@Component
public class FileCleaner {

    @Value("${project.code-dir}")
    private String codeDir;

    @PostConstruct
    public void init() {
        File dir = new File(codeDir);

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            delete(file);
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

    private void delete(File dir) {
        boolean emptyDir = true;

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            emptyDir = file.delete();
        }

        if (emptyDir) {
            if (!dir.delete())
                log.warn("Delete directory failed: path={}", dir.getAbsolutePath());
        }
    }
}

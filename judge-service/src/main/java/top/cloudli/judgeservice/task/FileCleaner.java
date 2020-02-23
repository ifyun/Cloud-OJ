package top.cloudli.judgeservice.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.cloudli.judgeservice.model.Language;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Objects;

@Slf4j
@Component
public class FileCleaner {

    @Value("${project.target-dir}")
    private String targetDir;

    @PostConstruct
    public void init() {
        File dir = new File(targetDir);

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            delete(file);
        }
    }

    @Async
    public void deleteTempFile(int solutionId, int languageId, boolean isWindows) {
        Language language = Language.get(languageId);

        if (language == null)
            return;

        switch (language) {
            case JAVA:
                delete(new File(targetDir + solutionId));
                break;
            case PYTHON:
                delete(new File(targetDir + solutionId + ".py"));
                break;
            case BASH:
                delete(new File(targetDir + solutionId + ".sh"));
                break;
            case C_CPP:
                delete(isWindows ? new File(targetDir + solutionId + ".exe")
                        : new File(targetDir + solutionId));
                delete(new File(targetDir + solutionId + ".cpp"));
                break;
        }
    }

    private void delete(File file) {
        if (file.delete())
            log.info("{} 已删除.", file.getName());
        else
            log.info("{} 删除失败.", file.getName());
    }
}

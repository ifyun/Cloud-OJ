package cloud.oj.judge.utils;

import cloud.oj.judge.config.AppConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * 文件清理，删除判题产生的临时代码文件
 */
@Slf4j
@Component
public class FileCleaner {

    private final AppConfig appConfig;

    @Autowired
    public FileCleaner(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @PostConstruct
    public void init() {
        FileUtils.deleteQuietly(new File(appConfig.getCodeDir()));
    }

    @Async
    public void deleteTempFile(String solutionId) {
        try {
            FileUtils.deleteDirectory(new File(appConfig.getCodeDir() + solutionId));
        } catch (IOException e) {
            log.warn("Delete solution dir failed: solutionId={}", solutionId);
        }
    }
}

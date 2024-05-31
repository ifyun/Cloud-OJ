package cloud.oj.judge.utils;

import cloud.oj.judge.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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

    public FileCleaner(AppConfig appConfig) {
        this.appConfig = appConfig;
        FileUtils.deleteQuietly(new File(appConfig.getCodeDir()));
    }

    @Async
    public void deleteTempFile(String solutionId) {
        try {
            FileUtils.deleteDirectory(new File(appConfig.getCodeDir() + solutionId));
        } catch (IOException e) {
            log.warn("删除失败: solutionId={}", solutionId);
        }
    }
}

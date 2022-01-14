package group._204.oj.judge.utils;

import group._204.oj.judge.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * 文件清理，删除判题产生的临时代码文件
 */
@Slf4j
@Component
public class FileCleaner {

    @Resource
    private AppConfig appConfig;

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

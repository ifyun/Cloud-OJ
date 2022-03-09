package group._204.oj.judge.service;

import feign.FeignException;
import feign.Response;
import group._204.oj.judge.client.FileService;
import group._204.oj.judge.config.AppConfig;
import group._204.oj.judge.config.AppHealth;
import group._204.oj.judge.model.FileInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试数据同步组件
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "app", name = "file-sync")
public class DataSyncService {

    @Resource
    private AppConfig appConfig;

    private String dataDir;

    @Resource
    private FileService fileService;

    @Resource
    private AppHealth appHealth;

    @PostConstruct
    private void init() {
        dataDir = appConfig.getFileDir() + "test_data";
        syncAllFiles();
    }

    /**
     * 同步所有测试数据
     */
    @SneakyThrows(InterruptedException.class)
    public void syncAllFiles() {
        try {
            List<FileInfo> remoteFiles = fileService.dataList();

            if (remoteFiles == null) {
                return;
            }

            Collections.sort(remoteFiles);

            List<FileInfo> localFiles = getLocalFiles();
            Collections.sort(localFiles);

            localFiles.forEach(localFile -> {
                // 在远程文件中找对应的本地文件
                int i = Collections.binarySearch(remoteFiles, localFile);
                if (i >= 0) {
                    // 本地远程都存在，更新
                    FileInfo remoteFile = remoteFiles.get(i);
                    if (remoteFile.getLastModified() != localFile.getLastModified()) {
                        downloadFile(remoteFile.getPath(), remoteFile.getLastModified());
                    }
                } else {
                    // 本地存在，远程不存在，删除
                    deleteFile(localFile.getPath());
                }
            });

            remoteFiles.forEach(remoteFile -> {
                // 在本地文件中找对应的远程文件
                if (Collections.binarySearch(localFiles, remoteFile) < 0) {
                    // 文件不存在，下载
                    downloadFile(remoteFile.getPath(), remoteFile.getLastModified());
                }
            });

            appHealth.setFileSynced(true);
            log.info("All test data has been synchronized.");
        } catch (FeignException e) {
            log.error("Sync test data failed: {}, {}", e.status(), e.getMessage());
            Thread.sleep(10000);
            syncAllFiles();
        } catch (IOException e) {
            appHealth.setFileSynced(false);
            appHealth.setDetail(e.getMessage());
            log.error(e.getMessage());
        }
    }

    public void syncFile(FileInfo fileInfo) {
        if (fileInfo.isDeleted()) {
            deleteFile(fileInfo.getPath());
        } else {
            downloadFile(fileInfo.getPath(), fileInfo.getLastModified());
        }
    }

    private List<FileInfo> getLocalFiles() throws IOException {
        return Files.walk(Paths.get(dataDir), Integer.MAX_VALUE)
                .filter(p -> p.toFile().isFile())
                .map(p -> {
                    String relativePath = p.toString().replace(dataDir, "");
                    return new FileInfo(relativePath, p.toFile().lastModified());
                })
                .collect(Collectors.toList());
    }

    /**
     * 下载文件
     *
     * @param path eg: /problemId/file
     * @param time 文件的修改时间
     */
    private void downloadFile(String path, long time) {
        Response res = fileService.downloadFile(path);

        if (res.status() == 200) {
            try {
                InputStream is = res.body().asInputStream();
                File dest = new File(dataDir + path);
                FileUtils.copyInputStreamToFile(is, dest);
                //noinspection ResultOfMethodCallIgnored
                dest.setLastModified(time);
                log.info("Sync file: {}", dest.getAbsolutePath());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            log.error("Sync {} failed: {}", path, res.status());
        }
    }

    /**
     * 删除文件
     *
     * @param path eg: /problemId/file
     */
    private void deleteFile(String path) {
        File file = new File(dataDir + path);

        if (!file.exists()) {
            return;
        }

        try {
            FileUtils.delete(file);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
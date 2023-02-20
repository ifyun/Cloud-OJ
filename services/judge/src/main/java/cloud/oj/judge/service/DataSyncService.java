package cloud.oj.judge.service;

import cloud.oj.judge.client.FileClient;
import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.config.AppHealth;
import cloud.oj.judge.entity.FileInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

/**
 * 测试数据同步组件
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "app", name = "file-sync")
public class DataSyncService {
    private final String dataDir;

    private final FileClient fileClient;

    private final AppHealth appHealth;

    private final ObjectMapper objectMapper;

    private final DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    @Autowired
    public DataSyncService(AppConfig appConfig, FileClient fileClient, AppHealth appHealth, ObjectMapper objectMapper) {
        dataDir = appConfig.getFileDir() + "test_data";
        this.fileClient = fileClient;
        this.appHealth = appHealth;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        log.info("文件同步已启用");
        new Thread(() -> {
            var synced = syncAllFiles();

            while (!synced) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synced = syncAllFiles();
            }

            log.info("全部测试数据已同步");
        }).start();
    }

    /**
     * 同步所有测试数据
     */
    public boolean syncAllFiles() {
        try {
            var osPipe = new PipedOutputStream();
            var isPipe = new PipedInputStream(osPipe);
            var reader = new BufferedReader(new InputStreamReader(isPipe));

            var data = fileClient.getAllFileInfo()
                    .onErrorResume(e -> {
                        log.error(e.getMessage());
                        return Mono.error(e);
                    })
                    .onErrorReturn(dataBufferFactory.wrap("error\n".getBytes()));

            DataBufferUtils.write(data, osPipe)
                    .doOnComplete(() -> {
                        try {
                            osPipe.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(DataBufferUtils.releaseConsumer());

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.equals("error")) {
                    return false;
                }

                var remoteFile = objectMapper.readValue(line, FileInfo.class);
                var localFile = new File(dataDir + remoteFile.getPath());

                // 文件在远程存在，在本地不存/不相同，下载
                if (!localFile.exists() || localFile.lastModified() != remoteFile.getLastModified()) {
                    if (!downloadFile(remoteFile.getPath(), remoteFile.getLastModified())) {
                        return false;
                    }
                }
            }

            localFiles(fileInfo -> {
                var path = URLEncoder.encode(fileInfo.getPath(), StandardCharsets.UTF_8);
                var remoteFile = fileClient.getFileInfo(path).block();
                // 文件在本地存在，在远程不存在，删除
                if (remoteFile == null) {
                    deleteFile(fileInfo.getPath());
                }
            });

            appHealth.setFileSynced(true);
            return true;
        } catch (Exception e) {
            log.error("同步文件失败: {}", e.getMessage());
            appHealth.setFileSynced(false);
            appHealth.setDetail(e.getMessage());
            return false;
        }
    }

    public void syncFile(FileInfo fileInfo) {
        if (fileInfo.isDeleted()) {
            deleteFile(fileInfo.getPath());
        } else {
            downloadFile(fileInfo.getPath(), fileInfo.getLastModified());
        }
    }

    /**
     * 遍历本地文件
     */
    private void localFiles(Consumer<FileInfo> c) throws IOException {
        try (var files = Files.walk(Paths.get(dataDir), Integer.MAX_VALUE)) {
            files.filter(p -> p.toFile().isFile())
                    .forEach(p -> {
                        String relativePath = p.toString().replace(dataDir, "");
                        c.accept(new FileInfo(relativePath, p.toFile().lastModified()));
                    });
        }
    }

    /**
     * 下载文件
     *
     * @param path 文件相对路径 eg: /problemId/file
     * @param time 文件的修改时间
     */
    private boolean downloadFile(String path, long time) {
        try {
            var dest = Paths.get(dataDir + path);
            var file = dest.toFile();

            if (!file.exists()) {
                FileUtils.forceMkdirParent(file);
            }

            var data = fileClient.downloadFile(path);
            DataBufferUtils.write(data, dest, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE).block();
            //noinspection ResultOfMethodCallIgnored
            file.setLastModified(time);
            log.info("下载: {}", path);
            return true;
        } catch (Exception e) {
            log.error("下载失败: {}, {}", path, e.getMessage());
            appHealth.setFileSynced(false);
            appHealth.setDetail(e.getMessage());
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param path 文件相对路径 eg: /problemId/file
     */
    private void deleteFile(String path) {
        var file = new File(dataDir + path);

        if (!file.exists()) {
            return;
        }

        try {
            FileUtils.delete(file);
            log.info("删除: {}", path);
        } catch (IOException e) {
            log.error(e.getMessage());
            appHealth.setFileSynced(false);
            appHealth.setDetail(e.getMessage());
        }
    }
}

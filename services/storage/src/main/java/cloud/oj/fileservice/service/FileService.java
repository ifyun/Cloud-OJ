package cloud.oj.fileservice.service;

import cloud.oj.fileservice.entity.FileInfo;
import cloud.oj.fileservice.entity.TestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileService {
    @Value("${app.file-dir}")
    private String fileDir;

    private final ObjectMapper objectMapper;

    private final NotifyService notifyService;

    @Autowired
    public FileService(NotifyService notifyService, ObjectMapper objectMapper) {
        this.notifyService = notifyService;
        this.objectMapper = objectMapper;
    }

    public List<TestData> getTestData(Integer problemId) {
        var files = new File(fileDir + "test_data/" + problemId).listFiles();
        var testDataList = new ArrayList<TestData>();

        if (files != null) {
            for (var file : files) {
                testDataList.add(new TestData(file.getName(), file.length()));
            }
        }

        return testDataList;
    }

    /**
     * 保存测试数据
     *
     * @param problemId 题目 Id
     * @param files     文件
     */
    public ResponseEntity<?> saveTestData(Integer problemId, MultipartFile[] files) {
        if (files.length == 0) {
            return ResponseEntity.badRequest().body("未选择文件.");
        }

        var testDataDir = fileDir + "test_data/";
        var dir = new File(testDataDir + problemId);

        if (!dir.exists() && !dir.mkdirs()) {
            log.error("无法创建目录 {}", dir.getAbsolutePath());
            return ResponseEntity.status(500).body("无法创建目录.");
        }

        for (MultipartFile file : files) {
            var fileName = file.getOriginalFilename();
            var dest = new File(dir.getAbsolutePath() + "/" + fileName);

            try {
                file.transferTo(dest);
                log.info("上传文件 {} ", dest.getAbsolutePath());
            } catch (IOException e) {
                log.error("上传文件 {} 失败: {}", dest.getAbsolutePath(), e.getMessage());
                return ResponseEntity.status(500).build();
            }

            notifyService.notifyTestData(dest, false);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除测试数据
     *
     * @param problemId 题目 Id
     * @param name      文件名
     */
    public ResponseEntity<?> deleteTestData(Integer problemId, String name) {
        var testDataDir = fileDir + "test_data/";
        var file = new File(testDataDir + problemId + "/" + name);

        if (file.exists()) {
            if (file.delete()) {
                log.info("已删除文件 {}", file.getAbsolutePath());
                notifyService.notifyTestData(file, true);
                return ResponseEntity.noContent().build();
            } else {
                log.error("删除文件 {} 失败", file.getAbsolutePath());
                return ResponseEntity.status(500).build();
            }
        } else {
            log.warn("文件 {} 不存在", file.getAbsolutePath());
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
    }


    public void writeFile(MultipartFile src, File dst) throws IOException {
        if (!dst.exists() && !dst.mkdirs()) {
            throw new IOException(String.format("无法创建目录, path: %s.", dst.getAbsolutePath()));
        }

        src.transferTo(dst);
        log.info("文件已保存, path={}.", dst.getAbsolutePath());
    }

    public boolean delFile(String filePath) {
        var file = new File(filePath);
        return !file.exists() || file.delete();
    }

    public void getFilesInfo(OutputStream os) {
        var dir = fileDir + "test_data";
        try (var files = Files.walk(Paths.get(dir), Integer.MAX_VALUE)) {
            files.filter(p -> p.toFile().isFile())
                    .forEach(p -> {
                        var relativePath = p.toString()
                                .replaceAll("\\\\", "/")
                                .replace(dir, "");
                        try {
                            var json = objectMapper.writeValueAsBytes(new FileInfo(relativePath, p.toFile().lastModified()));
                            os.write(json);
                            os.write("\n".getBytes());
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * @param path 文件相对路径 eg: /problemId/file
     * @return {@link FileInfo}
     */
    public FileInfo getFileInfo(String path) {
        var file = new File(fileDir + "test_data" + path);

        if (!file.exists()) {
            return null;
        }

        return new FileInfo(path, file.lastModified());
    }
}

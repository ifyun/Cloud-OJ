package cloud.oj.fileservice.controller;

import cloud.oj.fileservice.service.NotifyService;
import cloud.oj.fileservice.entity.TestData;
import cloud.oj.fileservice.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("test_data")
public class TestDataController {

    @Value("${app.file-dir}")
    private String fileDir;

    @Resource
    private NotifyService notifyService;

    /**
     * 获取所有测试数据文件和最后修改时间
     */
    @GetMapping(path = "info")
    public ResponseEntity<?> getTestDataInfo() {
        var fileInfos = FileUtil.getFilesInfo(fileDir + "test_data");
        return fileInfos.size() > 0 ? ResponseEntity.ok(fileInfos) : ResponseEntity.noContent().build();
    }

    /**
     * 获取测试数据列表
     */
    @GetMapping(path = "{problemId}")
    public ResponseEntity<?> getTestData(@PathVariable Integer problemId) {
        var files = new File(fileDir + "test_data/" + problemId).listFiles();
        var testDataList = new ArrayList<>();

        if (files != null) {
            for (var file : files) {
                testDataList.add(new TestData(file.getName(), file.length()));
            }
        }

        return testDataList.size() > 0 ? ResponseEntity.ok(testDataList) : ResponseEntity.noContent().build();
    }

    /**
     * 上传测试数据
     */
    @PostMapping
    public ResponseEntity<?> uploadTestData(@RequestParam Integer problemId,
                                            @RequestParam("file") MultipartFile[] files) {
        if (files.length == 0) {
            return ResponseEntity.badRequest().body("未选择文件.");
        }

        var testDataDir = fileDir + "test_data/";
        var dir = new File(testDataDir + problemId);

        if (!dir.exists() && !dir.mkdirs()) {
            log.error("无法创建目录 {}", dir.getName());
            return ResponseEntity.status(500).body("无法创建目录.");
        }

        for (MultipartFile file : files) {
            var fileName = file.getOriginalFilename();
            var dest = new File(dir + "/" + fileName);

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
     */
    @DeleteMapping(path = "{problemId}")
    public ResponseEntity<?> deleteTestData(@PathVariable Integer problemId, String name) {
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
}

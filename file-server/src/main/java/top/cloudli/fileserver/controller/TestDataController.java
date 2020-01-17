package top.cloudli.fileserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cloudli.fileserver.model.TestData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试数据文件上传与删除
 */
@Slf4j
@RestController
@RequestMapping("/test_data")
public class TestDataController {

    @Value("${project.test-data-dir}")
    private String testDataDir;

    @GetMapping("{problemId}")
    public ResponseEntity<?> getTestData(@PathVariable int problemId) {
        File dir = new File(testDataDir + problemId);
        File[] files = dir.listFiles();

        List<TestData> testDataList = new ArrayList<>();
        if (files != null)
            for (File file : files) {
                TestData testData = new TestData();
                // 读取文件名称
                testData.setFileName(file.getName());
                testData.setSize(file.length());

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                    // 读取文件内容
                    testData.setContent(reader.lines().collect(Collectors.joining("\n")));
                } catch (IOException ignored) {

                }

                testDataList.add(testData);
            }

        return testDataList.size() > 0 ?
                ResponseEntity.ok(testDataList) :
                ResponseEntity.noContent().build();
    }

    @PostMapping("")
    public ResponseEntity<?> uploadTestData(@RequestParam int problemId, @RequestParam("file") MultipartFile[] files) {
        if (files.length == 0)
            return ResponseEntity.badRequest().body("未选择文件.");

        // 根据题目 id 创建目录
        File dir = new File(testDataDir + problemId);

        if (!dir.exists()) {
            if (!dir.mkdir())
                return ResponseEntity.status(500).body("无法创建目录.");
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            File dest = new File(dir + "/" + fileName);

            try {
                file.transferTo(dest);
                log.info("上传文件 {} 到 {}", fileName, testDataDir);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(e.getMessage());
            }
        }

        return ResponseEntity.ok("{\"msg\": \"上传成功\"}");
    }

    @DeleteMapping("{problemId}")
    public ResponseEntity<?> deleteTestData(@PathVariable int problemId, String name) {
        File dest = new File(testDataDir + problemId + "/" + name);

        if (dest.delete()) {
            log.info("删除文件 {}/{}", problemId, name);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(500).body("删除文件失败");
    }
}

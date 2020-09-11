package group._204.oj.fileserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import group._204.oj.fileserver.model.TestData;

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

    /**
     * 获取测试数据文件
     *
     * @param problemId 题目 Id
     * @return {@link ResponseEntity<List>} 文件列表
     */
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

    /**
     * 上传测试数据文件
     *
     * @param problemId 题目 Id
     * @param files     文件
     * @return {@link ResponseEntity} 200：上传成功，500：上传失败
     */
    @PostMapping("")
    public ResponseEntity<?> uploadTestData(@RequestParam int problemId, @RequestParam("file") MultipartFile[] files) {
        if (files.length == 0)
            return ResponseEntity.badRequest().body("未选择文件.");

        // 根据题目 id 创建目录
        File dir = new File(testDataDir + problemId);

        if (!dir.exists()) {
            if (!dir.mkdir()) {
                log.error("无法创建目录 {}", dir.getName());
                return ResponseEntity.status(500).body("无法创建目录.");
            }
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            File dest = new File(dir + "/" + fileName);

            try {
                file.transferTo(dest);
                log.info("上传文件 {} 到 {}", fileName, testDataDir);
            } catch (IOException e) {
                log.error("上传文件 {} 失败: {}", fileName, e.getMessage());
                return ResponseEntity.status(500).body(e.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除测试数据文件
     *
     * @param problemId 题目 Id
     * @param name      文件名
     * @return {@link ResponseEntity} 204：删除成功，500：删除失败
     */
    @DeleteMapping("{problemId}")
    public ResponseEntity<?> deleteTestData(@PathVariable int problemId, String name) {
        File dest = new File(testDataDir + problemId + "/" + name);

        if (dest.delete()) {
            log.info("删除文件 {}/{}", problemId, name);
            return ResponseEntity.noContent().build();
        } else {
            log.error("删除文件 {}/{} 失败", problemId, name);
            return ResponseEntity.status(500).body("删除文件失败");
        }
    }
}

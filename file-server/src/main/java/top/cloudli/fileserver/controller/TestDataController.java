package top.cloudli.fileserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cloudli.fileserver.model.Result;

import java.io.File;
import java.io.IOException;

/**
 * 测试数据文件上传与删除
 */
@Slf4j
@RestController
@RequestMapping("/test_data")
public class TestDataController {

    @Value("${project.test-data-dir}")
    private String testDataDir;

    @PostMapping
    public Object uploadTestData(@RequestParam int problemId, @RequestParam MultipartFile[] files) {
        if (files.length == 0)
            return new Result(204, "未选择文件.");

        // 根据题目 id 创建目录
        File dir = new File(testDataDir + problemId);

        if (!dir.exists()) {
            if (!dir.mkdir())
                return new Result(500, "无法创建目录.");
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            File dest = new File(dir + "/" + fileName);

            try {
                file.transferTo(dest);
                log.info("上传文件 {} 到 {}", fileName, testDataDir);
            } catch (IOException e) {
                return new Result(500, e.getMessage());
            }
        }

        return new Result(200, "上传成功.");
    }

    @DeleteMapping
    public Object deleteTestData(String name) {
        File dest = new File(testDataDir + name);

        if (dest.delete()) {
            log.info("删除文件 {}", name);
            return new Result(200, "删除成功.");
        }

        return new Result(500, "删除失败.");
    }
}

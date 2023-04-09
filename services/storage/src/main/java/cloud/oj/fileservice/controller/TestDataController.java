package cloud.oj.fileservice.controller;

import cloud.oj.fileservice.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("test_data")
public class TestDataController {

    private final FileService fileService;

    @Autowired
    public TestDataController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 获取测试数据列表
     * <p>
     * 返回 {@link cloud.oj.fileservice.entity.TestData} 数组
     * </p>
     */
    @GetMapping(path = "{problemId}")
    public ResponseEntity<?> getTestData(@PathVariable Integer problemId) {
        var list = fileService.getTestData(problemId);
        return list.size() > 0 ? ResponseEntity.ok(list) : ResponseEntity.noContent().build();
    }

    /**
     * 上传测试数据
     */
    @PostMapping
    public ResponseEntity<?> uploadTestData(@RequestParam Integer problemId,
                                            @RequestParam("file") MultipartFile[] files) {
        return fileService.saveTestData(problemId, files);
    }


    /**
     * 删除测试数据
     */
    @DeleteMapping(path = "{problemId}")
    public ResponseEntity<?> deleteTestData(@PathVariable Integer problemId, String name) {
        return fileService.deleteTestData(problemId, name);
    }
}

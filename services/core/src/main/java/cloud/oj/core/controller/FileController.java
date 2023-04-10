package cloud.oj.core.controller;

import cloud.oj.core.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传头像
     */
    @PostMapping(path = "img/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestHeader String userId, @RequestParam MultipartFile file) {
        return fileService.saveAvatar(userId, file);
    }

    /**
     * 上传题目图片
     */
    @PostMapping(path = "img/problem")
    public ResponseEntity<?> uploadProblemImage(@RequestParam MultipartFile file) {
        return fileService.saveProblemImage(file);
    }

    /**
     * 获取测试数据列表
     * <p>
     * 返回 {@link cloud.oj.core.entity.TestData} 数组
     * </p>
     */
    @GetMapping(path = "data/{problemId}")
    public ResponseEntity<?> getTestData(@PathVariable Integer problemId) {
        var list = fileService.getTestData(problemId);
        return list.size() > 0 ? ResponseEntity.ok(list) : ResponseEntity.noContent().build();
    }

    /**
     * 上传测试数据
     */
    @PostMapping("data")
    public ResponseEntity<?> uploadTestData(@RequestParam Integer problemId,
                                            @RequestParam("file") MultipartFile[] files) {
        return fileService.saveTestData(problemId, files);
    }

    /**
     * 删除测试数据
     */
    @DeleteMapping(path = "data/{problemId}")
    public ResponseEntity<?> deleteTestData(@PathVariable Integer problemId, String name) {
        return fileService.deleteTestData(problemId, name);
    }
}

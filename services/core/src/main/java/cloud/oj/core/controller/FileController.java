package cloud.oj.core.controller;

import cloud.oj.core.entity.SPJ;
import cloud.oj.core.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传头像
     */
    @PostMapping(path = "img/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestHeader Integer uid, @RequestParam MultipartFile file) {
        return fileService.saveAvatar(uid, file);
    }

    /**
     * 上传题目图片
     */
    @PostMapping(path = "img/problem")
    public ResponseEntity<?> uploadProblemImage(@RequestParam MultipartFile file) {
        return fileService.saveProblemImage(file);
    }

    /**
     * 获取题目数据
     */
    @GetMapping(path = "data/{problemId}")
    public ResponseEntity<?> getProblemData(@PathVariable Integer problemId) {
        return ResponseEntity.ok(fileService.getProblemData(problemId));
    }

    /**
     * 上传测试数据
     */
    @PostMapping(path = "data")
    public ResponseEntity<?> uploadTestData(@RequestParam Integer pid,
                                            @RequestParam("file") MultipartFile[] files) {
        return fileService.saveTestData(pid, files);
    }

    /**
     * 删除测试数据
     */
    @DeleteMapping(path = "data/{problemId}")
    public ResponseEntity<?> deleteTestData(@PathVariable Integer problemId, String name) {
        return fileService.deleteTestData(problemId, name);
    }

    @PostMapping(path = "spj")
    public ResponseEntity<?> uploadSPJ(@RequestBody SPJ spj) {
        System.out.println(spj.getSource());
        return fileService.saveSPJ(spj);
    }

    @DeleteMapping(path = "spj/{pid}")
    public ResponseEntity<?> deleteSPJ(@PathVariable Integer pid) {
        return fileService.removeSPJ(pid);
    }
}

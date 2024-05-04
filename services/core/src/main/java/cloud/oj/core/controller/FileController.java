package cloud.oj.core.controller;

import cloud.oj.core.entity.SPJ;
import cloud.oj.core.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传头像
     */
    @PostMapping(path = "img/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestHeader Integer uid, @RequestParam MultipartFile file) {
        return ResponseEntity.status(fileService.saveAvatar(uid, file)).build();
    }

    /**
     * 上传题目图片
     */
    @PostMapping(path = "img/problem")
    public ResponseEntity<String> uploadProblemImage(@RequestParam MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fileService.saveProblemImage(file));
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
        return ResponseEntity.status(fileService.saveTestData(pid, files)).build();
    }

    /**
     * 删除测试数据
     */
    @DeleteMapping(path = "data/{problemId}")
    public ResponseEntity<?> deleteTestData(@PathVariable Integer problemId, String name) {
        return ResponseEntity.status(fileService.deleteTestData(problemId, name)).build();
    }

    /**
     * 上传 SPJ 代码
     */
    @PostMapping(path = "spj")
    public ResponseEntity<?> uploadSPJ(@RequestBody SPJ spj) {
        return ResponseEntity.status(fileService.saveSPJ(spj)).build();
    }

    /**
     * 删除 SPJ
     */
    @DeleteMapping(path = "spj/{pid}")
    public ResponseEntity<?> deleteSPJ(@PathVariable Integer pid) {
        return ResponseEntity.status(fileService.removeSPJ(pid)).build();
    }
}

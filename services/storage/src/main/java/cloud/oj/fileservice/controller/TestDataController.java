package cloud.oj.fileservice.controller;

import cloud.oj.fileservice.service.FileService;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
     * 获取所有测试数据文件的信息
     *
     * <p>
     * 在每一行中写入 {@link cloud.oj.fileservice.entity.FileInfo} 实例的 JSON 字符串
     * </p>
     *
     * <p>
     * 返回的数据需要按行读取
     * </p>
     */
    @GetMapping(path = "file_info_list")
    public void getTestDataInfo(ServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        fileService.getFilesInfo(response.getOutputStream());
    }

    /**
     * 获取单个测试数据的文件信息
     *
     * <p>
     * 返回 {@link cloud.oj.fileservice.entity.FileInfo}
     * </p>
     *
     * @param file 文件相对路径 eg: /problemId/file
     */
    @GetMapping(path = "file_info")
    public ResponseEntity<?> getFileInfo(@RequestParam("file") String file) {
        var fileInfo = fileService.getFileInfo(file);
        return fileInfo != null ? ResponseEntity.ok(fileInfo) : ResponseEntity.noContent().build();
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

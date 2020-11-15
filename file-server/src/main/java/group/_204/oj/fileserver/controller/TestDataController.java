package group._204.oj.fileserver.controller;

import group._204.oj.fileserver.model.TestData;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test_data")
@Api(tags = "测试数据")
public class TestDataController {

    @Value("${project.file-dir}")
    private String fileDir;

    @ApiOperation(value = "获取测试数据列表", notes = "需要题目管理员权限")
    @ApiImplicitParam(name = "problemId", required = true, example = "1001")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = TestData.class, responseContainer = "List"),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "{problemId}", produces = "application/json")
    public ResponseEntity<?> getTestData(@PathVariable Integer problemId) {
        String testDataDir = fileDir + "test_data/";
        File dir = new File(testDataDir + problemId);
        File[] files = dir.listFiles();

        List<TestData> testDataList = new ArrayList<>();
        if (files != null)
            for (File file : files) {
                TestData testData = new TestData();
                // 读取文件名称
                testData.setFileName(file.getName());
                testData.setSize(file.length());

                try (BufferedReader reader =
                             new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
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

    @ApiOperation(value = "上传测试数据", notes = "需要题目管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problemId", required = true, example = "1001"),
            @ApiImplicitParam(name = "file", value = "测试数据文件(*.in, *.out)", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "上传成功"),
            @ApiResponse(code = 500, message = "无法写入文件")
    })
    @PostMapping
    public ResponseEntity<?> uploadTestData(@RequestParam Integer problemId,
                                            @RequestParam("file") MultipartFile[] files) {
        if (files.length == 0)
            return ResponseEntity.badRequest().body("未选择文件.");

        // 根据题目 id 创建目录
        String testDataDir = fileDir + "test_data/";
        File dir = new File(testDataDir + problemId);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.error("无法创建目录 {}", dir.getName());
                return ResponseEntity.status(500).body("无法创建目录.");
            }
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            File dest = new File(dir + "/" + fileName);

            try {
                file.transferTo(dest);
                log.info("上传文件 {} ", dest.getAbsolutePath());
            } catch (IOException e) {
                log.error("上传文件 {} 失败: {}", dest.getAbsolutePath(), e.getMessage());
                return ResponseEntity.status(500).build();
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @ApiOperation(value = "删除测试数据文件", notes = "需要题目管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problemId", required = true, example = "1001"),
            @ApiImplicitParam(name = "name", value = "文件名称", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 204, message = "删除成功"),
            @ApiResponse(code = 500, message = "无法删除文件")
    })
    @DeleteMapping(path = "{problemId}")
    public ResponseEntity<?> deleteTestData(@PathVariable Integer problemId, String name) {
        String testDataDir = fileDir + "test_data/";
        File file = new File(testDataDir + problemId + "/" + name);

        if (file.exists()) {
            if (file.delete()) {
                log.info("已删除文件 {}", file.getAbsolutePath());
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

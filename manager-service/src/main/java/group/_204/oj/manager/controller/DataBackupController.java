package group._204.oj.manager.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import group._204.oj.manager.model.Problem;
import group._204.oj.manager.service.ProblemService;
import io.swagger.annotations.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("backup")
@Api(tags = "题目导入/导出")
public class DataBackupController {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ProblemService problemService;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    /**
     * 导出题目
     */
    @SneakyThrows
    @ApiOperation(value = "导出题目", notes = "需要题目管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "导出成功", response = Problem.class, responseContainer = "List"),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(produces = "application/json")
    public void backupProblems(HttpServletResponse response) {
        List<Problem> problems = problemService.backup();

        if (problems.size() > 0) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=Problems_Backup_%s.json", dateFormat.format(new Date())));
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(problems));
        } else {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }

    @SneakyThrows
    @ApiOperation(value = "导入题目", notes = "需要题目管理员权限")
    @ApiImplicitParam(name = "file", value = ".json 文件", dataTypeClass = MultipartFile.class, required = true)
    @ApiResponses({
            @ApiResponse(code = 201, message = "导入成功"),
            @ApiResponse(code = 400, message = "导入失败")
    })
    @PostMapping
    public ResponseEntity<?> importProblems(@RequestParam("file") MultipartFile file) {
        try {
            List<Problem> problems = objectMapper.readValue(file.getBytes(),
                    new TypeReference<List<Problem>>() {
                    });
            problemService.importProblems(problems);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (JsonParseException | JsonMappingException e) {
            log.error("Cannot import problems: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

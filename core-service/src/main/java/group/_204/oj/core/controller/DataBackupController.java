package group._204.oj.core.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import group._204.oj.core.model.Problem;
import group._204.oj.core.service.ProblemService;
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
    @GetMapping()
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

    /**
     * 导入题目
     */
    @SneakyThrows
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

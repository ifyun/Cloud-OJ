package group._204.oj.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group._204.oj.manager.model.Problem;
import group._204.oj.manager.service.ProblemService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("")
    @SneakyThrows
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

    @PostMapping("")
    @SneakyThrows
    public ResponseEntity<?> importProblems(@RequestBody List<Problem> problems) {
        problemService.importProblems(problems);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

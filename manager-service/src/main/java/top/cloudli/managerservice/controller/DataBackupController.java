package top.cloudli.managerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cloudli.managerservice.dao.ProblemDao;
import top.cloudli.managerservice.model.Problem;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class DataBackupController {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ProblemDao problemDao;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    @SneakyThrows
    @GetMapping("pro/backup/problem")
    public void backup(HttpServletResponse response) {
        List<Problem> problems = problemDao.backup();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Content-Disposition",
                String.format("attachment; filename=PROBLEMS_BACKUP_%s.json", dateFormat.format(new Date())));
        response.setCharacterEncoding("utf-8");

        if (problems.size() > 0) {
            response.getWriter().write(objectMapper.writeValueAsString(problems));
        } else {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }
}

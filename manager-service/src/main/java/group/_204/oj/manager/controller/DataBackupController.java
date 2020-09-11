package group._204.oj.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group._204.oj.manager.model.Problem;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import group._204.oj.manager.dao.ProblemDao;
import group._204.oj.manager.model.TestData;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("pro/backup")
public class DataBackupController {

    @Value("${project.test-data-dir}")
    private String testDataDir;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ProblemDao problemDao;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    /**
     * 备份题目
     */
    @SneakyThrows
    @GetMapping("problem")
    public void backupProblems(HttpServletResponse response) {
        List<Problem> problems = problemDao.backup();

        if (problems.size() > 0) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=Problems_Backup_%s.json", dateFormat.format(new Date())));
            response.setCharacterEncoding("utf-8");
            log.info("Backup problems.");
            // 加入测试数据
            for (Problem problem : problems) {
                int id = problem.getProblemId();
                List<String> input = getTestData(id, ".in"),
                        output = getTestData(id, ".out");
                problem.setTestData(new TestData(input, output));
                log.info("Took test data of problem {}", id);
            }

            response.getWriter().write(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(problems)
            );
        } else {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }

    /**
     * 获取测试数据
     *
     * @param id  题目 Id
     * @param ext 扩展名
     * @return 测试数据集合
     */
    private List<String> getTestData(int id, String ext) {
        List<String> data = null;
        File dir = new File(testDataDir + id);

        File[] files = dir.listFiles(pathname -> {
            String name = pathname.getName();
            return name.substring(name.lastIndexOf('.')).equals(ext);
        });

        if (files != null) {
            data = new ArrayList<>(files.length);
            for (File file : files) {
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(file))
                    );
                    data.add(reader.lines().collect(Collectors.joining("\n")));
                } catch (FileNotFoundException e) {
                    log.error(e.getMessage());
                }
            }
        }

        return data;
    }
}

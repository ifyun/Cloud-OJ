package group._204.oj.manager.service;

import group._204.oj.manager.dao.ContestDao;
import group._204.oj.manager.dao.ProblemDao;
import group._204.oj.manager.model.Msg;
import group._204.oj.manager.model.Problem;
import group._204.oj.manager.model.TestData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProblemService {

    @Value("${project.file-dir}")
    private String fileDir;

    @Resource
    private ProblemDao problemDao;

    @Resource
    private ContestDao contestDao;

    public List<List<?>> getAllEnabled(String keyword, int page, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = null;
        }
        return problemDao.getAllEnabled((page - 1) * limit, limit, keyword);
    }

    public List<List<?>> getAll(String keyword, int page, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = null;
        }
        return problemDao.getAll((page - 1) * limit, limit, keyword);
    }

    public List<List<?>> getAllWithState(String keyword, String userId, int page, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = null;
        }
        return problemDao.getWithState((page - 1) * limit, limit, userId, keyword);
    }

    public Problem getSingle(int problemId) {
        return problemDao.getSingle(problemId, false);
    }

    public Problem getSingleEnabled(int problemId) {
        return problemDao.getSingle(problemId, true);
    }

    @Transactional
    public Msg update(Problem problem) {
        Integer contestId = problemDao.isInContest(problem.getProblemId());
        if (contestId != null && contestDao.getContest(contestId).isStarted()) {
            return new Msg(400, "不能修改已开始竞赛/作业中的题目");
        }
        int status = problemDao.update(problem) == 1 ? 200 : 304;
        return new Msg(status, null);
    }

    @Transactional
    public Msg toggleEnable(int problemId, boolean enable) {
        if (enable) {
            Integer contestId = problemDao.isInContest(problemId);
            if (contestId != null && !contestDao.getContest(contestId).isEnded()) {
                return new Msg(400, "不能开放未结束竞赛/作业中的题目");
            }
        }
        int status = problemDao.toggleEnable(problemId, enable) == 1 ? 200 : 304;
        return new Msg(status, null);
    }

    public boolean add(Problem problem) {
        int row = problemDao.add(problem);
        log.info("Add Problem: id={}, title={}", problem.getProblemId(), problem.getTitle());
        return row == 1;
    }

    @Transactional
    public List<Problem> backup() {
        List<Problem> problems = problemDao.backup();

        // 加入测试数据
        for (Problem problem : problems) {
            int id = problem.getProblemId();
            List<String> input = getTestData(id, ".in"),
                    output = getTestData(id, ".out");
            problem.setTestData(new TestData(input, output));
        }

        log.info("Backup problems, count={}", problems.size());

        return problems;
    }

    public Msg delete(Integer problemId) {
        if (problemDao.isInContest(problemId) != null) {
            return new Msg(400, "无法删除竞赛/作业中的题目");
        }

        int row = problemDao.delete(problemId);
        int status = row == 1 ? 204 : 410;

        return new Msg(status, null);
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
        String testDataDir = fileDir + "test_data/";
        File dir = new File(testDataDir + id);

        File[] files = dir.listFiles(pathname -> {
            String name = pathname.getName();
            return name.substring(name.lastIndexOf('.')).equals(ext);
        });

        if (files != null && files.length > 0) {
            data = new ArrayList<>(files.length);
            List<File> fileList = Arrays.stream(files)
                    .sorted(Comparator.comparing(File::getName))
                    .collect(Collectors.toList());

            for (File file : fileList) {
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

    /**
     * 导入题目
     */
    @Transactional
    public void importProblems(List<Problem> problems) throws Exception {
        for (Problem p : problems) {
            if (add(p)) {
                if (!writeTestData(p.getProblemId(), p.getTestData()))
                    throw new IOException("Cannot write test data.");
            } else {
                throw new SQLException("Cannot add problem.");
            }
        }
    }

    private boolean writeTestData(int problemId, TestData data) throws IOException {
        File dir = new File(fileDir + "test_data/" + problemId);

        if (!dir.mkdirs()) {
            return false;
        }

        write(dir, data.getInput(), ".in");
        write(dir, data.getOutput(), ".out");

        return true;
    }

    private void write(File dir, List<String> dataList, String ext) throws IOException {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            FileWriter writer = new FileWriter(dir + "/" + i + ext);
            writer.write(dataList.get(i));
            writer.flush();
            writer.close();
        }
    }
}

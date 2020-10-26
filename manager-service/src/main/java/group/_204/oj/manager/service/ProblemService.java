package group._204.oj.manager.service;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProblemService {

    @Value("${project.file-dir}")
    private String fileDir;

    @Resource
    private ProblemDao problemDao;

    public List<List<?>> getEnableProblems(String userId, int page, int limit) {
        return problemDao.getAll(userId, (page - 1) * limit, limit, true);
    }

    public List<List<?>> getProblems(String userId, int page, int limit) {
        return problemDao.getAll(userId, (page - 1) * limit, limit, false);
    }

    public Problem getProblem(int problemId) {
        return problemDao.getSingle(problemId, false);
    }

    public Problem getEnableProblem(int problemId) {
        return problemDao.getSingle(problemId, true);
    }

    public List<List<?>> searchProblems(String userId, String keyword, int page, int limit, boolean enable) {
        return problemDao.search(userId, keyword, (page - 1) * limit, limit, enable);
    }

    @Transactional
    public Msg updateProblem(Problem problem) {
        if (problemDao.inContest(problem.getProblemId()) > 0) {
            return new Msg(400, "无法修改竞赛/作业中的题目");
        }
        int status = problemDao.update(problem) == 1 ? 200 : 304;
        return new Msg(status, null);
    }

    @Transactional
    public Msg toggleEnable(int problemId, boolean enable) {
        if (enable) {
            if (problemDao.inContest(problemId) > 0) {
                return new Msg(400, "无法开放竞赛/作业中的题目");
            }
        }
        int status = problemDao.toggleEnable(problemId, enable) == 1 ? 200 : 304;
        return new Msg(status, null);
    }

    public boolean addProblem(Problem problem) {
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

    public Msg deleteProblem(int problemId) {
        if (problemDao.inContest(problemId) > 0) {
            return new Msg(400, "无法删除竞赛/作业中的题目");
        }

        int row = problemDao.delete(problemId);

        if (row == 1) {
            log.info("Delete problem: id={}", problemId);
        } else {
            log.error("Delete problem failed, id={}", problemId);
        }

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

    /**
     * 导入题目
     */
    @Transactional
    public void importProblems(List<Problem> problems) throws Exception {
        for (Problem p : problems) {
            if (addProblem(p)) {
                if (!writeTestData(p.getProblemId(), p.getTestData()))
                    throw new IOException("Cannot write test data.");
            } else {
                throw new Exception("Cannot add problem.");
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
        for (int i = 0; i < dataList.size(); i++) {
            FileWriter writer = new FileWriter(new File(dir + "/" + i + ext));
            writer.write(dataList.get(i));
            writer.flush();
            writer.close();
        }
    }
}

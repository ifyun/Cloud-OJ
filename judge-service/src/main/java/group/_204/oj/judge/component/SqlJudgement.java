package group._204.oj.judge.component;

import group._204.oj.judge.config.AppConfig;
import group._204.oj.judge.dao.DatabaseConfig;
import group._204.oj.judge.dao.ProblemDao;
import group._204.oj.judge.dao.RuntimeDao;
import group._204.oj.judge.dao.SolutionDao;
import group._204.oj.judge.model.Runtime;
import group._204.oj.judge.model.Solution;
import group._204.oj.judge.type.SolutionResult;
import group._204.oj.judge.type.SolutionState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;

/**
 * SQL 判题
 * <p>基于 SQLite</p>
 */
@Slf4j
@Component
public class SqlJudgement {

    @Resource
    private AppConfig appConfig;

    @Resource
    private ProblemDao problemDao;

    @Resource
    private RuntimeDao runtimeDao;

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private DatabaseConfig dbConfig;

    @Data
    @AllArgsConstructor
    static class Result {
        boolean correct;
        long time;
    }

    @Transactional(rollbackFor = Exception.class)
    public void judge(Solution solution) {
        log.info("Judging: type(SQL), solution({}), user({}).", solution.getSolutionId(), solution.getUserId());
        dbConfig.disableFKChecks();

        String correctSql = problemDao.getSql(solution.getProblemId()); // 获取正确的 SQL 语句
        File[] files = new File(appConfig.getFileDir() + "test_data/" + solution.getProblemId())
                .listFiles(file -> file.getName().endsWith(".db"));
        Runtime runtime = new Runtime(solution.getSolutionId());
        runtimeDao.add(runtime);

        if (files == null) {
            String err = "Test data(*.db file) required.";
            runtime.setResult(SolutionResult.IE);
            runtime.setInfo(err);
            log.error(err);
        } else {
            try {
                Result r = judge(solution.getSourceCode(), correctSql, files[0].getAbsolutePath());
                runtime.setResult(r.correct ? SolutionResult.AC : SolutionResult.WA);
                runtime.setTotal(1);
                runtime.setPassed(r.correct ? 1 : 0);
                runtime.setTime(r.time);
                runtime.setMemory(0L);
            } catch (InterruptedException | IOException e) {
                log.warn(e.getMessage());
                runtime.setResult(SolutionResult.RE);
                runtime.setInfo(e.getMessage());
            }
        }

        solution.setState(SolutionState.JUDGED);
        solution.setResult(runtime.getResult());
        runtimeDao.update(runtime);
        solutionDao.update(solution);
    }

    /**
     * 判题
     * <p>先后执行正确的语句和用户的语句，对比输出</p>
     *
     * @param userSql    用户的 SQL 语句
     * @param correctSql 正确的 SQL 语句
     * @param dbFile     数据库文件路径
     * @return {@link Result} 结果
     */
    private Result judge(String userSql, String correctSql, String dbFile) throws InterruptedException, IOException {
        InputStream correctOutput = exec(correctSql, dbFile);
        InputStream userOutput = exec(userSql, dbFile);

        return diff(userOutput, correctOutput);
    }

    private InputStream exec(String sql, String dbFile) throws IOException {
        String CONFIG = ".timer on\r\n.mode column\r\n.header on\r\n";
        ProcessBuilder builder = new ProcessBuilder("sqlite3", "file:" + dbFile + "?mode=ro");
        Process process = builder.start();
        OutputStream stdin = process.getOutputStream();
        stdin.write(CONFIG.getBytes());
        stdin.write((sql + ";\r\n.quit\r\n").getBytes());
        stdin.flush();

        return process.getInputStream();
    }

    private Result diff(InputStream userStdout, InputStream correctStdout) throws IOException {
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(userStdout));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(correctStdout));

        String line1, line2;
        boolean end1, end2, correct = false;
        long time;

        while (true) {
            line1 = reader1.readLine();
            line2 = reader2.readLine();
            end1 = line1.startsWith("Run Time");
            end2 = line2.startsWith("Run Time");

            if (end1) {
                time = readTime(line1);
                break;
            } else if (end2) {
                String t;
                while ((t = reader1.readLine()) != null) {
                    line1 = t;
                }
                time = readTime(line1);
                break;
            }

            correct = line1.equals(line2);
        }

        if (end1 != end2) {
            correct = false;
        }

        reader1.close();
        reader2.close();

        return new Result(correct, time);
    }

    private long readTime(String str) {
        double userTime = Double.parseDouble(str.substring(26, 34));
        double sysTime = Double.parseDouble(str.substring(39));
        return (long) ((userTime + sysTime) * 1000);
    }
}
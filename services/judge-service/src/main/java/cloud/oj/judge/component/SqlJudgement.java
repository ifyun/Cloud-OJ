package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.dao.DatabaseConfig;
import cloud.oj.judge.dao.ProblemDao;
import cloud.oj.judge.dao.RuntimeDao;
import cloud.oj.judge.dao.SolutionDao;
import cloud.oj.judge.entity.Runtime;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.enums.SolutionResult;
import cloud.oj.judge.enums.SolutionState;
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

        var correctSql = problemDao.getSql(solution.getProblemId()); // 获取正确的 SQL 语句
        var files = new File(appConfig.getFileDir() + "test_data/" + solution.getProblemId())
                .listFiles(file -> file.getName().endsWith(".db"));
        var runtime = new Runtime(solution.getSolutionId());
        runtimeDao.add(runtime);

        if (files == null) {
            var err = "Test data(*.db file) required.";
            runtime.setResult(SolutionResult.IE);
            runtime.setInfo(err);
            log.error(err);
        } else {
            try {
                var r = judge(solution.getSourceCode(), correctSql, files[0].getAbsolutePath());
                runtime.setResult(r.correct ? SolutionResult.AC : SolutionResult.WA);
                runtime.setTotal(1);
                runtime.setPassed(r.correct ? 1 : 0);
                runtime.setTime(r.time);
                runtime.setMemory(0L);
            } catch (IOException e) {
                log.warn(e.getMessage());
                runtime.setResult(SolutionResult.RE);
                runtime.setInfo(e.getMessage());
            }
        }

        solution.setState(SolutionState.JUDGED);
        solution.setResult(runtime.getResult());
        runtimeDao.update(runtime);
        solutionDao.updateState(solution);
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
    private Result judge(String userSql, String correctSql, String dbFile) throws IOException {
        var correctOutput = exec(correctSql, dbFile);
        var userOutput = exec(userSql, dbFile);

        return diff(userOutput, correctOutput);
    }

    private InputStream exec(String sql, String dbFile) throws IOException {
        var CONFIG = ".timer on\r\n.mode column\r\n.header on\r\n";
        var builder = new ProcessBuilder("sqlite3", "file:" + dbFile + "?mode=ro");
        var process = builder.start();
        var stdin = process.getOutputStream();
        stdin.write(CONFIG.getBytes());
        stdin.write((sql + ";\r\n.quit\r\n").getBytes());
        stdin.flush();

        return process.getInputStream();
    }

    private Result diff(InputStream userStdout, InputStream correctStdout) throws IOException {
        var reader1 = new BufferedReader(new InputStreamReader(userStdout));
        var reader2 = new BufferedReader(new InputStreamReader(correctStdout));

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
        var userTime = Double.parseDouble(str.substring(26, 34));
        var sysTime = Double.parseDouble(str.substring(39));
        return (long) ((userTime + sysTime) * 1000);
    }
}
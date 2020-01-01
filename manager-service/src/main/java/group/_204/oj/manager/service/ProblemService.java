package group._204.oj.manager.service;

import group._204.oj.manager.dao.ProblemDao;
import group._204.oj.manager.model.Msg;
import group._204.oj.manager.model.Problem;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProblemService {

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

    public Msg updateProblem(Problem problem) {
        if (problemDao.inContest(problem.getProblemId()) > 0) {
            return new Msg(400, "无法修改竞赛/作业中的题目");
        }
        int status = problemDao.update(problem) == 1 ? 200 : 304;
        return new Msg(status, null);
    }

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
        return problemDao.add(problem) == 1;
    }

    public Msg deleteProblem(int problemId) {
        if (problemDao.inContest(problemId) > 0) {
            return new Msg(400, "无法删除竞赛/作业中的题目");
        }
        int status = problemDao.delete(problemId) == 1 ? 204 : 410;
        return new Msg(status, null);
    }
}

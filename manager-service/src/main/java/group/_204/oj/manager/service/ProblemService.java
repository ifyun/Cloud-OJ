package group._204.oj.manager.service;

import group._204.oj.manager.dao.ProblemDao;
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

    public boolean updateProblem(Problem problem) {
        return problemDao.update(problem) == 1;
    }

    public boolean toggleEnable(int problemId, boolean enable) {
        return problemDao.toggleEnable(problemId, enable) == 1;
    }

    public boolean addProblem(Problem problem) {
        return problemDao.add(problem) == 1;
    }

    public boolean deleteProblem(int problemId) {
        return problemDao.delete(problemId) == 1;
    }
}

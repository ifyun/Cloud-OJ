package group._204.oj.core.service;

import group._204.oj.core.dao.ContestDao;
import group._204.oj.core.dao.ProblemDao;
import group._204.oj.core.model.Msg;
import group._204.oj.core.model.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ProblemService {
    @Resource
    private ProblemDao problemDao;

    @Resource
    private ContestDao contestDao;

    public List<List<?>> getAllEnabled(String keyword, int page, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            return problemDao.getAllEnabled((page - 1) * limit, limit, null);
        }

        return problemDao.getAllEnabled((page - 1) * limit, limit, keyword);
    }

    public List<List<?>> getAll(String keyword, int page, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            problemDao.getAll((page - 1) * limit, limit, null);
        }

        return problemDao.getAll((page - 1) * limit, limit, keyword);
    }

    public List<List<?>> getAllWithState(String keyword, String userId, int page, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            problemDao.getWithState((page - 1) * limit, limit, userId, null);
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
            return new Msg(400, "不能修改已开始竞赛中的题目");
        }
        int status = problemDao.update(problem) == 1 ? 200 : 304;
        return new Msg(status, null);
    }

    @Transactional
    public Msg toggleEnable(int problemId, boolean enable) {
        if (enable) {
            Integer contestId = problemDao.isInContest(problemId);
            if (contestId != null && !contestDao.getContest(contestId).isEnded()) {
                return new Msg(400, "不能开放未结束竞赛中的题目");
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

    public Msg delete(Integer problemId) {
        if (problemDao.isInContest(problemId) != null) {
            return new Msg(400, "无法删除竞赛中的题目");
        }

        int row = problemDao.delete(problemId);
        int status = row == 1 ? 204 : 410;

        return new Msg(status, null);
    }
}

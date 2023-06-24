package cloud.oj.core.service;

import cloud.oj.core.dao.ContestDao;
import cloud.oj.core.dao.ProblemDao;
import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.error.GenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContestService {

    private final ContestDao contestDao;

    private final ProblemDao problemDao;

    @Autowired
    public ContestService(ContestDao contestDao, ProblemDao problemDao) {
        this.contestDao = contestDao;
        this.problemDao = problemDao;
    }

    public List<List<?>> getAllContest(int page, int limit) {
        return contestDao.getContests((page - 1) * limit, limit, false);
    }

    public Optional<Contest> getContest(int contestId) {
        return Optional.ofNullable(contestDao.getContestById(contestId));
    }

    public List<List<?>> getStartedContest(int page, int limit) {
        return contestDao.getContests((page - 1) * limit, limit, true);
    }

    public HttpStatus addContest(Contest contest) {
        if (contestDao.addContest(contest) == 1) {
            return HttpStatus.CREATED;
        } else {
            throw new GenericException(400, "请求数据可能不正确");
        }
    }

    public HttpStatus updateContest(Contest contest) {
        if (contestDao.updateContest(contest) == 1) {
            return HttpStatus.OK;
        } else {
            throw new GenericException(400, String.format("竞赛(%d)更新失败", contest.getContestId()));
        }
    }

    /**
     * 逻辑删除竞赛
     * <p>已开始未结束的竞赛不允许删除</p>
     *
     * @param contestId 竞赛 Id
     * @return HTTP 状态码 {@link HttpStatus}
     */
    public HttpStatus deleteContest(int contestId) {
        var contest = contestDao.getState(contestId);

        if (contest.isStarted() && !contest.isEnded()) {
            throw new GenericException(400, "竞赛已开始，不准删除");
        }

        if (contestDao.deleteContest(contestId) == 1) {
            return HttpStatus.NO_CONTENT;
        } else {
            throw new GenericException(410, String.format("竞赛(%d)不存在", contestId));
        }
    }

    public List<List<?>> getProblemsNotInContest(int contestId, int page, int limit) {
        return contestDao.getProblemsNotInContest(contestId, (page - 1) * limit, limit);
    }

    public List<Problem> getProblemsFromContest(String userId, int contestId, boolean onlyStarted) {
        if (userId == null) {
            return contestDao.getProblems(contestId, onlyStarted);
        }

        return contestDao.getProblemsWithResult(userId, contestId);
    }

    public Optional<Problem> getProblemInContest(Integer contestId, Integer problemId) {
        return Optional.ofNullable(problemDao.getByIdFromContest(contestId, problemId));
    }

    public HttpStatus addProblemToContest(int contestId, int problemId) {
        if (contestDao.addProblem(contestId, problemId) == 1) {
            return HttpStatus.CREATED;
        } else {
            throw new GenericException(400, "无法添加题目");
        }
    }

    public HttpStatus removeProblem(int contestId, int problemId) {
        if (contestDao.removeProblem(contestId, problemId) == 1) {
            return HttpStatus.NO_CONTENT;
        } else {
            throw new GenericException(410, String.format("题目(%d)不存在", problemId));
        }
    }
}

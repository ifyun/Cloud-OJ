package cloud.oj.core.service;

import cloud.oj.core.dao.ContestDao;
import cloud.oj.core.dao.ProblemDao;
import cloud.oj.core.error.GenericException;
import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class ContestService {

    @Resource
    private ContestDao contestDao;

    @Resource
    private ProblemDao problemDao;

    public List<List<?>> getAllContest(int page, int limit) {
        return contestDao.getContests((page - 1) * limit, limit, false);
    }

    public Optional<Contest> getContest(int contestId) {
        return Optional.ofNullable(contestDao.getContest(contestId));
    }

    public List<List<?>> getStartedContest(int page, int limit) {
        return contestDao.getContests((page - 1) * limit, limit, true);
    }

    public Integer addContest(Contest contest) {
        if (contestDao.addContest(contest) == 1) {
            return HttpStatus.CREATED.value();
        } else {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "请求数据可能不正确");
        }
    }

    public Integer updateContest(Contest contest) {
        if (contestDao.updateContest(contest) == 1) {
            return HttpStatus.OK.value();
        } else {
            throw new GenericException(
                    HttpStatus.NOT_MODIFIED.value(),
                    String.format("竞赛 %d 更新失败", contest.getContestId())
            );
        }
    }

    public Integer deleteContest(int contestId) {
        if (contestDao.deleteContest(contestId) == 1) {
            return HttpStatus.NO_CONTENT.value();
        } else {
            throw new GenericException(
                    HttpStatus.GONE.value(),
                    String.format("竞赛 %d 不存在", contestId)
            );
        }
    }

    public List<List<?>> getProblemsNotInContest(int contestId, int page, int limit) {
        return contestDao.getProblemsNotInContest(contestId, (page - 1) * limit, limit);
    }

    public List<Problem> getProblemsFromContest(String userId, int contestId, boolean onlyStarted) {
        return contestDao.getProblems(userId, contestId, onlyStarted);
    }

    public Optional<Problem> getProblemInContest(Integer contestId, Integer problemId) {
        return Optional.ofNullable(problemDao.getSingleInContest(contestId, problemId));
    }

    public Integer addProblemToContest(int contestId, int problemId) {
        if (contestDao.addProblem(contestId, problemId) == 1){
            return HttpStatus.CREATED.value();
        } else {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "无法添加题目");
        }
    }

    public Integer removeProblem(int contestId, int problemId) {
        if (contestDao.removeProblem(contestId, problemId) == 1) {
            return HttpStatus.NO_CONTENT.value();
        } else {
            throw new GenericException(HttpStatus.GONE.value(), String.format("题目 %d 不存在", problemId));
        }
    }
}

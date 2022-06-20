package group._204.oj.core.service;

import group._204.oj.core.dao.ContestDao;
import group._204.oj.core.dao.ProblemDao;
import group._204.oj.core.model.Contest;
import group._204.oj.core.model.Msg;
import group._204.oj.core.model.Problem;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContestService {

    @Resource
    private ContestDao contestDao;

    @Resource
    private ProblemDao problemDao;

    public List<List<?>> getAllContest(int page, int limit) {
        return contestDao.getContests((page - 1) * limit, limit, false);
    }

    public Contest getContest(int contestId) {
        return contestDao.getContest(contestId);
    }

    public List<List<?>> getStartedContest(int page, int limit) {
        return contestDao.getContests((page - 1) * limit, limit, true);
    }

    public boolean addContest(Contest contest) {
        return contestDao.addContest(contest) == 1;
    }

    public Msg updateContest(Contest contest) {
        int status = contestDao.updateContest(contest) == 1 ? 200 : 304;
        return new Msg(status);
    }

    public Msg deleteContest(int contestId) {
        int status = contestDao.deleteContest(contestId) == 1 ? 204 : 401;
        return new Msg(status);
    }

    public List<List<?>> getProblemsNotInContest(int contestId, int page, int limit) {
        return contestDao.getProblemsNotInContest(contestId, (page - 1) * limit, limit);
    }

    public List<Problem> getProblemsFromContest(String userId, int contestId, boolean onlyStarted) {
        return contestDao.getProblems(userId, contestId, onlyStarted);
    }

    public Problem getProblemInContest(Integer contestId, Integer problemId) {
        return problemDao.getSingleInContest(contestId, problemId);
    }

    public boolean addProblemToContest(int contestId, int problemId) {
        return contestDao.addProblem(contestId, problemId) == 1;
    }

    public Msg removeProblem(int contestId, int problemId) {
        int status = contestDao.removeProblem(contestId, problemId) == 1 ? 204 : 410;
        return new Msg(status);
    }
}

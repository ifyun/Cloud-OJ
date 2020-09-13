package group._204.oj.manager.service;

import group._204.oj.manager.dao.ContestDao;
import group._204.oj.manager.model.Contest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContestService {

    @Resource
    private ContestDao contestDao;

    public List<List<?>> getAllContest(int page, int limit) {
        return contestDao.getAllContest((page - 1) * limit, limit);
    }

    public Contest getLanguages(int contestId) {
        return contestDao.getContest(contestId);
    }

    public List<List<?>> getStartedContest(int page, int limit) {
        return contestDao.getStartedContest((page - 1) * limit, limit);
    }

    public boolean addContest(Contest contest) {
        return contestDao.addContest(contest) == 1;
    }

    public boolean updateContest(Contest contest) {
        return contestDao.updateContest(contest) == 1;
    }

    public boolean deleteContest(int contestId) {
        return contestDao.deleteContest(contestId) == 1;
    }

    public List<List<?>> getProblemsNotInContest(int contestId, int page, int limit) {
        return contestDao.getProblemsNotInContest(contestId, (page - 1) * limit, limit);
    }

    public List<List<?>> getProblemsFromContest(String userId, int contestId, int page, int limit) {
        return contestDao.getProblems(userId, contestId, (page - 1) * limit, limit);
    }

    public boolean addProblemToContest(int contestId, int problemId) {
        return contestDao.addProblem(contestId, problemId) == 1;
    }

    public boolean deleteProblemFromContest(int contestId, int problemId) {
        return contestDao.deleteProblem(contestId, problemId) == 1;
    }
}

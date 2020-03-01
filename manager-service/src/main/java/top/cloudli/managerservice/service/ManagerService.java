package top.cloudli.managerservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.cloudli.managerservice.dao.*;
import top.cloudli.managerservice.model.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ManagerService {

    @Resource
    private ProblemDao problemDao;

    @Resource
    private UserDao userDao;

    @Resource
    private ContestDao contestDao;

    @Resource
    private JudgeResultDao judgeResultDao;

    @Resource
    private RankingDao rankingDao;

    // NOTE 题目

    public List<List<?>> getEnableProblems(String userId, int start, int limit) {
        return problemDao.getAll(userId, start, limit, true);
    }

    public List<List<?>> getProblems(String userId, int start, int limit) {
        return problemDao.getAll(userId, start, limit, false);
    }

    public Problem getProblem(int problemId) {
        return problemDao.getSingle(problemId, false);
    }

    public Problem getEnableProblem(int problemId) {
        return problemDao.getSingle(problemId, true);
    }

    public List<List<?>> searchProblems(String userId, String keyword, int start, int limit, boolean enable) {
        return problemDao.search(userId, keyword, start, limit, enable);
    }

    public boolean updateProblem(Problem problem) {
        return problemDao.update(problem) == 1;
    }

    public boolean updateProblemEnable(int problemId, boolean enable) {
        return problemDao.updateEnable(problemId, enable) == 1;
    }

    public String addProblem(Problem problem) {
        return problemDao.add(problem) == 1 ? null : "添加失败.";
    }

    public boolean deleteProblem(int problemId) {
        return problemDao.delete(problemId) == 1;
    }

    // NOTE 用户

    public List<List<?>> getUsers(int start, int limit) {
        return userDao.getAll(start, limit);
    }

    public String addUser(User user) {
        return userDao.add(user) == 1 ? null : "添加失败.";
    }

    public boolean updateUser(User user) {
        return userDao.update(user) == 1;
    }

    public boolean deleteUser(int userId) {
        return userDao.delete(userId) == 1;
    }

    // NOTE 获取提交记录

    public List<List<?>> getJudged(String userId, int start, int limit) {
        return judgeResultDao.getByUserId(userId, start, limit);
    }

    // NOTE 排行

    public List<List<?>> getRanking(int start, int limit) {
        return rankingDao.getRanking(start, limit);
    }

    // NOTE 竞赛排行

    public List<List<?>> getContestRanking(int contestId, int start, int limit) {
        return rankingDao.getContestRanking(contestId, start, limit);
    }

    // NOTE 竞赛/作业

    public List<List<?>> getAllContest(int start, int limit) {
        return contestDao.getAllContest(start, limit);
    }

    public Contest getLanguages(int contestId) {
        return contestDao.getContest(contestId);
    }

    public List<List<?>> getStartedContest(int start, int limit) {
        return contestDao.getStartedContest(start, limit);
    }

    public String addContest(Contest contest) {
        return contestDao.addContest(contest) == 1 ? null : "添加失败.";
    }

    public boolean updateContest(Contest contest) {
        return contestDao.updateContest(contest) == 1;
    }

    public boolean deleteContest(int contestId) {
        return contestDao.deleteContest(contestId) == 1;
    }

    public List<List<?>> getProblemsNotInContest(int contestId, int start, int limit) {
        return contestDao.getProblemsNotInContest(contestId, start, limit);
    }

    public List<List<?>> getProblemsFromContest(String userId, int contestId, int start, int limit) {
        return contestDao.getProblems(userId, contestId, start, limit);
    }

    public String addProblemToContest(int contestId, int problemId) {
        return contestDao.addProblem(contestId, problemId) == 1 ? null : "添加失败.";
    }

    public boolean deleteProblemFromContest(int contestId, int problemId) {
        return contestDao.deleteProblem(contestId, problemId) == 1;
    }
}

package top.cloudli.managerservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import top.cloudli.managerservice.dao.*;
import top.cloudli.managerservice.model.*;

import javax.annotation.Resource;

@Slf4j
@Service
public class ManagerService implements CRUDService {

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

    public ResponseEntity<?> getEnableProblems(String userId, int start, int limit) {
        return buildGETResponse(problemDao.getAll(userId, start, limit, true));
    }

    public ResponseEntity<?> getProblems(String userId, int start, int limit) {
        return buildGETResponse(problemDao.getAll(userId, start, limit, false));
    }

    public ResponseEntity<?> getProblem(int problemId) {
        return buildGETSingleResponse(problemDao.getSingle(problemId, false));
    }

    public ResponseEntity<?> getEnableProblem(int problemId) {
        return buildGETSingleResponse(problemDao.getSingle(problemId, true));
    }

    public ResponseEntity<?> searchProblems(String userId, String keyword, int start, int limit, boolean enable) {
        String title = String.format("%%%s%%", keyword);
        return buildGETResponse(problemDao.search(userId, title, start, limit, enable));
    }

    public ResponseEntity<Void> updateProblem(Problem problem) {
        return buildPUTResponse(problemDao.update(problem) == 1);
    }

    public ResponseEntity<Void> updateProblemEnable(int problemId, boolean enable) {
        return buildPUTResponse(problemDao.updateEnable(problemId, enable) == 1);
    }

    public ResponseEntity<?> addProblem(Problem problem) {
        String error = problemDao.add(problem) == 1 ? null : "添加失败.";
        return buildPOSTResponse(error);
    }

    public ResponseEntity<Void> deleteProblem(int problemId) {
        return buildDELETEResponse(problemDao.delete(problemId) == 1);
    }

    // NOTE 用户

    public ResponseEntity<?> getUsers(int start, int limit) {
        return buildGETResponse(userDao.getAll(start, limit));
    }

    public ResponseEntity<?> addUser(User user) {
        String error = userDao.add(user) == 1 ? null : "添加失败.";
        return buildPOSTResponse(error);
    }

    public ResponseEntity<Void> updateUser(User user) {
        return buildPUTResponse(userDao.update(user) == 1);
    }

    public ResponseEntity<Void> deleteUser(int userId) {
        return buildDELETEResponse(userDao.delete(userId) == 1);
    }

    // NOTE 获取提交记录

    public ResponseEntity<?> getJudged(String userId, int start, int limit) {
        return buildGETResponse(judgeResultDao.getByUserId(userId, start, limit));
    }

    // NOTE 排行

    public ResponseEntity<?> getRanking(int start, int limit) {
        return buildGETResponse(rankingDao.getRanking(start, limit));
    }

    // NOTE 竞赛排行

    public ResponseEntity<?> getContestRanking(int contestId, int start, int limit) {
        return buildGETResponse(rankingDao.getContestRanking(contestId, start, limit));
    }

    // NOTE 竞赛/作业

    public ResponseEntity<?> getAllContest(int start, int limit) {
        return buildGETResponse(contestDao.getAllContest(start, limit));
    }

    public ResponseEntity<?> getStartedContest(int start, int limit) {
        return buildGETResponse(contestDao.getStartedContest(start, limit));
    }

    public ResponseEntity<?> addContest(Contest contest) {
        String error = contestDao.addContest(contest) == 1 ? null : "添加失败.";
        return buildPOSTResponse(error);
    }

    public ResponseEntity<?> updateContest(Contest contest) {
        return buildPUTResponse(contestDao.updateContest(contest) == 1);
    }

    public ResponseEntity<?> deleteContest(int contestId) {
        return buildDELETEResponse(contestDao.deleteContest(contestId) == 1);
    }

    public ResponseEntity<?> getProblemsNotInContest(int contestId, int start, int limit) {
        return buildGETResponse(contestDao.getProblemsNotInContest(contestId, start, limit));
    }

    public ResponseEntity<?> getProblemsFromContest(String userId, int contestId, int start, int limit) {
        return buildGETResponse(contestDao.getProblems(userId, contestId, start, limit));
    }

    public ResponseEntity<?> addProblemToContest(int contestId, int problemId) {
        String error = contestDao.addProblem(contestId, problemId) == 1 ? null : "添加失败.";
        return buildPOSTResponse(error);
    }

    public ResponseEntity<?> deleteProblemFromContest(int contestId, int problemId) {
        return buildDELETEResponse(contestDao.deleteProblem(contestId, problemId) == 1);
    }
}

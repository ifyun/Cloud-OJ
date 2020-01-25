package top.cloudli.managerservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.cloudli.managerservice.dao.*;
import top.cloudli.managerservice.model.*;

import javax.annotation.Resource;
import java.util.List;

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

    @Transactional
    public ResponseEntity<?> getEnableProblems(String userId, int start, int limit) {
        long total = problemDao.getEnableCount();
        List<Problem> problems = problemDao.getEnable(userId, start, limit);
        return buildGETResponse(new PagedResult<>(total, problems));
    }

    @Transactional
    public ResponseEntity<?> getProblems(String userId, int start, int limit) {
        long total = problemDao.getCount();
        List<Problem> problems = problemDao.getAll(userId, start, limit);
        return buildGETResponse(new PagedResult<>(total, problems));
    }

    public ResponseEntity<?> getProblem(int problemId) {
        return buildGETSingleResponse(problemDao.getSingle(problemId));
    }

    public ResponseEntity<?> getEnableProblem(int problemId) {
        return buildGETSingleResponse(problemDao.getSingleEnable(problemId));
    }

    @Transactional
    public ResponseEntity<?> searchProblems(String userId, String keyword, int start, int limit) {
        String title = String.format("%%%s%%", keyword);
        long total = problemDao.getSearchCount(title);
        List<Problem> problems = problemDao.searchByTitle(userId, title, start, limit);
        return buildGETResponse(new PagedResult<>(total, problems));
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

    @Transactional
    public ResponseEntity<?> getUsers(int start, int limit) {
        long total = userDao.getCount();
        List<User> users = userDao.getAll(start, limit);
        return buildGETResponse(new PagedResult<>(total, users));
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

    @Transactional
    public ResponseEntity<?> getJudged(String userId, int start, int limit) {
        long total = judgeResultDao.getCount(userId);
        List<JudgeResult> results = judgeResultDao.getByUserId(userId, start, limit);
        return buildGETResponse(new PagedResult<>(total, results));
    }

    // NOTE 获取排行

    @Transactional
    public ResponseEntity<?> getRanking(int start, int limit) {
        long total = rankingDao.getCount();
        List<Ranking> rankingList = rankingDao.getRanking(start, limit);
        return buildGETResponse(new PagedResult<>(total, rankingList));
    }

    // NOTE 竞赛/作业

    @Transactional
    public ResponseEntity<?> getAllContest(int start, int limit) {
        long total = contestDao.getAllCount();
        List<Contest> contests = contestDao.getAllContest(start, limit);
        return buildGETResponse(new PagedResult<>(total, contests));
    }

    @Transactional
    public ResponseEntity<?> getStartedContest(int start, int limit) {
        long total = contestDao.getStartedCount();
        List<Contest> contests = contestDao.getStartedContest(start, limit);
        return buildGETResponse(new PagedResult<>(total, contests));
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

    @Transactional
    public ResponseEntity<?> getProblemsNotInContest(int contestId, int start, int limit) {
        long total = contestDao.getNotInContestCount(contestId);
        List<Problem> problems = contestDao.getProblemsNotInContest(contestId, start, limit);
        return buildGETResponse(new PagedResult<>(total, problems));
    }

    @Transactional
    public ResponseEntity<?> getProblemsFromContest(String userId, int contestId, int start, int limit) {
        long total = contestDao.getProblemsCount(contestId);
        List<Problem> problems = contestDao.getProblems(userId, contestId, start, limit);
        return buildGETResponse(new PagedResult<>(total, problems));
    }

    public ResponseEntity<?> addProblemToContest(int contestId, int problemId) {
        String error = contestDao.addProblem(contestId, problemId) == 1 ? null : "添加失败.";
        return buildPOSTResponse(error);
    }

    public ResponseEntity<?> deleteProblemFromContest(int contestId, int problemId) {
        return buildDELETEResponse(contestDao.deleteProblem(contestId, problemId) == 1);
    }
}

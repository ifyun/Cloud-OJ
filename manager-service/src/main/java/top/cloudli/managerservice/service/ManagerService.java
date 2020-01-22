package top.cloudli.managerservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
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

    // NOTE 题目管理部分

    public ResponseEntity<?> getEnableProblems(int start, int limit) {
        List<Problem> problems = problemDao.getEnable(start, limit);
        return buildGETResponse(new PagedResult<>(problemDao.getEnableCount(), problems));
    }

    public ResponseEntity<?> getProblems(int start, int limit) {
        List<Problem> problems = problemDao.getAll(start, limit);
        return buildGETResponse(new PagedResult<>(problemDao.getCount(), problems));
    }

    public ResponseEntity<?> getProblem(int problemId) {
        return buildGETSingleResponse(problemDao.getSingle(problemId));
    }

    public ResponseEntity<?> getEnableProblem(int problemId) {
        return buildGETSingleResponse(problemDao.getSingleEnable(problemId));
    }

    public ResponseEntity<?> searchProblems(String keyword, int start, int limit) {
        String title = String.format("%%%s%%", keyword);
        List<Problem> problems = problemDao.searchByTitle(title, start, limit);
        return buildGETResponse(new PagedResult<>(problemDao.getSearchCount(title), problems));
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


    // NOTE 用户管理部分

    public ResponseEntity<?> getUsers(int start, int limit) {
        List<User> users = userDao.getAll(start, limit);
        return buildGETResponse(new PagedResult<>(userDao.getCount(), users));
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
        List<JudgeResult> results = judgeResultDao.getByUserId(userId, start, limit);
        return buildGETResponse(new PagedResult<>(judgeResultDao.getCount(userId), results));
    }

    // NOTE 获取排行

    public ResponseEntity<?> getRanking(int start, int limit) {
        List<Ranking> rankingList = rankingDao.getRanking(start, limit);
        return buildGETResponse(new PagedResult<>(rankingDao.getCount(), rankingList));
    }

    // NOTE 竞赛/作业管理

    public ResponseEntity<?> getAllContest(int start, int limit) {
        List<Contest> contests = contestDao.getAllContest(start, limit);
        return buildGETResponse(new PagedResult<>(contestDao.getCount(), contests));
    }

    public ResponseEntity<?> addContest(Contest contest) {
        String error = contestDao.addContest(contest) == 1 ? null : "添加失败.";
        return buildPOSTResponse(error);
    }

    public ResponseEntity<?> getProblemsFromContest(int contestId, int start, int limit) {
        List<Problem> problems = contestDao.getProblems(contestId, start, limit);
        return buildGETResponse(new PagedResult<>(contestDao.getProblemsCount(contestId), problems));
    }

    public ResponseEntity<?> deleteContest(int contestId) {
        return buildDELETEResponse(contestDao.deleteContest(contestId) == 1);
    }

    public ResponseEntity<?> addProblemToContest(int contestId, int problemId) {
        String error = contestDao.addProblem(contestId, problemId) == 1 ? null : "添加失败.";
        return buildPOSTResponse(error);
    }

    public ResponseEntity<?> deleteProblemFromContest(int contestId, int problemId) {
        return buildDELETEResponse(contestDao.deleteProblem(contestId, problemId) == 1);
    }
}

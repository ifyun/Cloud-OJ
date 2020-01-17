package top.cloudli.managerservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.dao.JudgeResultDao;
import top.cloudli.managerservice.dao.ProblemDao;
import top.cloudli.managerservice.dao.UserDao;
import top.cloudli.managerservice.model.JudgeResult;
import top.cloudli.managerservice.model.PagedResult;
import top.cloudli.managerservice.model.Problem;
import top.cloudli.managerservice.model.User;

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
    private JudgeResultDao judgeResultDao;

    // 题目管理部分

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


    // 用户管理部分

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

    // 获取提交记录

    public ResponseEntity<?> getJudged(String userId, int start, int limit) {
        List<JudgeResult> results = judgeResultDao.getByUserId(userId, start, limit);
        return buildGETResponse(new PagedResult<>(judgeResultDao.getCount(userId), results));
    }
}

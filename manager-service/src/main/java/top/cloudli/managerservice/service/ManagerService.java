package top.cloudli.managerservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.dao.ProblemDao;
import top.cloudli.managerservice.dao.UserDao;
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

    public ResponseEntity<?> getEnableProblems() {
        List<Problem> problems = problemDao.getEnable();
        return buildGETResponse(problems);
    }

    public ResponseEntity<?> getProblems() {
        List<Problem> problems = problemDao.getAll();
        return buildGETResponse(problems);
    }

    public ResponseEntity<?> getProblem(int problemId) {
        return buildGETSingleResponse(problemDao.getSingle(problemId));
    }

    public ResponseEntity<?> getEnableProblem(int problemId) {
        return buildGETSingleResponse(problemDao.getSingleEnable(problemId));
    }

    public ResponseEntity<?> searchProblems(String keyword) {
        List<Problem> problems = problemDao.searchByTitle(String.format("%%%s%%", keyword));
        return buildGETResponse(problems);
    }

    public ResponseEntity<Void> updateProblem(Problem problem) {
        return buildPUTResponse(problemDao.update(problem) == 1);
    }

    public ResponseEntity<?> addProblem(Problem problem) {
        String error = problemDao.add(problem) == 1 ? null : "添加失败.";
        return buildPOSTResponse(error);
    }

    public ResponseEntity<Void> deleteProblem(int problemId) {
        return buildDELETEResponse(problemDao.delete(problemId) == 1);
    }


    // 用户管理部分

    public ResponseEntity<?> getUsers() {
        List<User> users = userDao.getAll();
        return buildGETResponse(users);
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
}
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

    @Value("${server.port}")
    private String port;

    @Value("${spring.application.name}")
    private String app;

    public ResponseEntity<?> getEnableProblems() {
        log.debug("{}:{} 获取已启用题目.", app, port);
        List<Problem> problems = problemDao.getEnable();
        return buildGETResponse(problems);
    }

    public ResponseEntity<?> getProblems() {
        log.debug("{}:{} 获取所有题目.", app, port);
        List<Problem> problems = problemDao.getAll();
        return buildGETResponse(problems);
    }

    public ResponseEntity<?> searchProblems(String keyword) {
        log.debug("{}:{} 搜索题目.", app, port);
        List<Problem> problems = problemDao.searchByTitle(String.format("%%%s%%", keyword));
        return buildGETResponse(problems);
    }

    public ResponseEntity<Void> updateProblem(Problem problem) {
        log.debug("{}:{} 修改题目.", app, port);
        return buildPUTResponse(problemDao.update(problem) == 1);
    }

    public ResponseEntity<?> addProblem(Problem problem) {
        log.debug("{}:{} 添加题目.", app, port);
        String error = problemDao.add(problem) == 1 ? null : "添加失败.";
        return buildPOSTResponse(error);
    }

    public ResponseEntity<Void> deleteProblem(int problemId) {
        log.debug("{}:{} 删除题目.", app, port);
        return buildDELETEResponse(problemDao.delete(problemId) == 1);
    }


    // 用户管理部分

    public ResponseEntity<?> getUsers() {
        log.debug("{}:{} 获取用户.", app, port);
        List<User> users = userDao.getAll();
        return buildGETResponse(users);
    }

    public ResponseEntity<?> addUser(@RequestBody User user) {
        log.debug("{}:{} 添加用户.", app, port);
        String error = userDao.add(user) == 1 ? null : "添加失败.";
        return buildPOSTResponse(error);
    }

    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        log.debug("{}:{} 修改用户.", app, port);
        return buildPUTResponse(userDao.update(user) == 1);
    }

    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        log.debug("{}:{} 删除用户.", app, port);
        return buildDELETEResponse(userDao.delete(userId) == 1);
    }
}

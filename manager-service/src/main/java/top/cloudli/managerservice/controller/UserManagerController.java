package top.cloudli.managerservice.controller;

import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.dao.UserDao;
import top.cloudli.managerservice.data.Result;
import top.cloudli.managerservice.model.User;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserManagerController {

    @Resource
    private UserDao userDao;

    @GetMapping("")
    public Result getUsers() {
        return new Result(200, "success", userDao.getAll());
    }

    @PostMapping("")
    public Result addUser(@RequestBody User user) {
        return new Result(200, "success", userDao.add(user));
    }

    @PutMapping("")
    public Result updateUser(@RequestBody User user) {
        return new Result(200, "success", userDao.add(user));
    }

    @DeleteMapping("{userId}")
    public Result deleteUser(@PathVariable int userId) {
        return new Result(200, "success", userDao.delete(userId));
    }
}

package top.cloudli.managerservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.dao.UserDao;
import top.cloudli.managerservice.model.User;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserManagerController {

    @Resource
    private UserDao userDao;

    @GetMapping("")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userDao.getAll();
        return users.size() != 0 ?
                ResponseEntity.ok(users) :
                ResponseEntity.noContent().build();
    }

    @PostMapping("")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        return userDao.add(user) == 1 ?
                ResponseEntity.ok("注册成功.") :
                ResponseEntity.badRequest().body("注册失败.");
    }

    @PutMapping("")
    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        return userDao.update(user) == 1 ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        return userDao.delete(userId) == 1 ?
                ResponseEntity.noContent().build() :
                ResponseEntity.status(HttpStatus.GONE).build();
    }
}

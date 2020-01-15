package top.cloudli.managerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.model.User;
import top.cloudli.managerservice.service.ManagerService;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class UserManagerController {

    @Resource
    private ManagerService managerService;

    @GetMapping("pro")
    public ResponseEntity<?> getUsers(int page, int limit) {
        return managerService.getUsers((page - 1) * limit, limit);
    }

    @PostMapping("")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return managerService.addUser(user);
    }

    @PutMapping("pro")
    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        return managerService.updateUser(user);
    }

    @DeleteMapping("pro/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        return managerService.deleteUser(userId);
    }
}

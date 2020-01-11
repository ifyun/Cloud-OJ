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

    @GetMapping("")
    public ResponseEntity<?> getUsers() {
        return managerService.getUsers();
    }

    @PostMapping("")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return managerService.addUser(user);
    }

    @PutMapping("")
    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        return managerService.updateUser(user);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        return managerService.deleteUser(userId);
    }
}

package top.cloudli.managerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.model.User;
import top.cloudli.managerservice.service.ManagerService;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class UserController implements CRUDController {

    @Resource
    private ManagerService managerService;

    @GetMapping("pro")
    public ResponseEntity<?> getUsers(int page, int limit) {
        return buildGETResponse(managerService.getUsers((page - 1) * limit, limit));
    }

    @PostMapping("")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return buildPOSTResponse(managerService.addUser(user));
    }

    @PutMapping("pro")
    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        return buildPUTResponse(managerService.updateUser(user));
    }

    @DeleteMapping("pro/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        return buildDELETEResponse(managerService.deleteUser(userId));
    }
}

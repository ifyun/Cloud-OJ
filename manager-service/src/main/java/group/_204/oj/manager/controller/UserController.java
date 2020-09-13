package group._204.oj.manager.controller;

import group._204.oj.manager.model.User;
import group._204.oj.manager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class UserController implements CRUDController {

    @Resource
    private UserService userService;

    @GetMapping("pro")
    public ResponseEntity<?> getUsers(int page, int limit) {
        return buildGETResponse(userService.getUsers(page, limit));
    }

    @PostMapping("")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return buildPOSTResponse(userService.addUser(user));
    }

    @PutMapping("pro")
    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        return buildPUTResponse(userService.updateUser(user));
    }

    @DeleteMapping("pro/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        return buildDELETEResponse(userService.deleteUser(userId));
    }
}

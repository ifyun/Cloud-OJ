package group._204.oj.manager.controller;

import group._204.oj.manager.model.User;
import group._204.oj.manager.service.OverviewService;
import group._204.oj.manager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class UserController implements CRUDController {

    @Resource
    private UserService userService;

    @Resource
    private OverviewService overviewService;

    @GetMapping("overview")
    public ResponseEntity<?> getOverview(String userId, Integer year) {
        return buildGETResponse(overviewService.getOverview(userId, year));
    }

    @GetMapping("pro")
    public ResponseEntity<?> getUsers(int page, int limit) {
        return buildGETResponse(userService.getUsers(page, limit));
    }

    @PostMapping("")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return buildPOSTResponse(userService.addUser(user));
    }

    @PutMapping("pro")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        return buildPUTResponse(userService.updateUser(user));
    }

    @PutMapping("profile")
    public ResponseEntity<?> updateProfile(@RequestParam String userId,
                                           @RequestBody User user) {
        return buildPUTResponse(userService.updateProfile(userId, user));
    }

    @DeleteMapping("pro/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        return buildDELETEResponse(userService.deleteUser(userId));
    }
}

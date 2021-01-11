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

    /**
     * 获取统计信息
     */
    @GetMapping(path = "overview")
    public ResponseEntity<?> getOverview(String userId, Integer year) {
        return buildGETResponse(userService.getOverview(userId, year));
    }

    /**
     * 获取所有用户
     */
    @GetMapping(path = "pro")
    public ResponseEntity<?> getUsers(Integer page, Integer limit) {
        return buildGETResponse(userService.getUsers(page, limit));
    }

    /**
     * 获取用户的个人信息
     */
    @GetMapping(path = "profile")
    public ResponseEntity<?> getUserInfo(String userId) {
        return buildGETResponse(userService.getUserInfo(userId));
    }

    /**
     * 添加/注册用户
     */
    @PostMapping()
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return buildPOSTResponse(userService.addUser(user));
    }

    /**
     * 更新用户(管理员)
     */
    @PutMapping(path = "pro")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        return buildPUTResponse(userService.updateUser(user));
    }

    /**
     * 更新用户(个人)
     */
    @PutMapping(path = "profile")
    public ResponseEntity<?> updateProfile(@RequestHeader String userId,
                                           @RequestBody User user) {
        return buildPUTResponse(userService.updateProfile(userId, user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping(path = "pro/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        return buildDELETEResponse(userService.deleteUser(userId));
    }
}

package cloud.oj.core.controller;

import cloud.oj.core.entity.PagedList;
import cloud.oj.core.entity.User;
import cloud.oj.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取统计信息
     *
     * @return {@link java.util.HashMap}
     */
    @GetMapping(path = "overview")
    public ResponseEntity<?> getOverview(String userId, Integer year, String tz) {
        return ResponseEntity.ok(userService.getOverview(userId, year, tz));
    }

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    @GetMapping(path = "admin")
    public ResponseEntity<?> getUsers(Integer page, Integer limit, String userId, String name) {
        var users = PagedList.resolve(userService.getUsers(page, limit, userId, name));
        return users.getCount() > 0 ?
                ResponseEntity.ok(users)
                : ResponseEntity.noContent().build();
    }

    /**
     * 获取用户的个人信息
     *
     * @return {@link User}
     */
    @GetMapping(path = "profile")
    public ResponseEntity<?> getUserInfo(String userId) {
        return userService.getUserInfo(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 添加/注册用户
     */
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        user.setRoleId(1);
        return ResponseEntity.status(userService.addUser(user)).build();
    }

    /**
     * 更新用户(管理员)
     */
    @PutMapping(path = "admin")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        return ResponseEntity.status(userService.updateUser(user)).build();
    }

    /**
     * 更新用户(个人)
     */
    @PutMapping(path = "profile")
    public ResponseEntity<?> updateProfile(@RequestHeader String userId, @RequestBody User user) {
        return ResponseEntity.status(userService.updateProfile(userId, user)).build();
    }

    /**
     * 删除用户(管理员)
     */
    @DeleteMapping(path = "admin/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        return ResponseEntity.status(userService.deleteUser(userId)).build();
    }
}

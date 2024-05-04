package cloud.oj.core.controller;

import cloud.oj.core.entity.User;
import cloud.oj.core.entity.UserStatistic;
import cloud.oj.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取统计信息
     *
     * @param tz 时区
     */
    @GetMapping(path = "overview")
    public ResponseEntity<UserStatistic> getOverview(Integer uid, Integer year, String tz) {
        return ResponseEntity.ok(userService.getOverview(uid, year, tz));
    }

    /**
     * 根据过滤条件获取用户
     */
    @GetMapping(path = "admin")
    public ResponseEntity<?> getUsers(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "15") Integer limit,
                                      Integer filter,
                                      String filterValue) {
        var data = userService.getUsersByFilter(filter, filterValue, page, limit);
        return data.getTotal() > 0 ?
                ResponseEntity.ok(data) :
                ResponseEntity.noContent().build();
    }

    /**
     * 获取用户的个人信息
     */
    @GetMapping(path = "profile")
    public ResponseEntity<User> getUserInfo(Integer uid) {
        return ResponseEntity.ok(userService.getUserInfo(uid));
    }

    /**
     * 添加/注册用户
     */
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
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
    public ResponseEntity<?> updateProfile(@RequestHeader Integer uid, @RequestBody User user) {
        return ResponseEntity.status(userService.updateProfile(uid, user)).build();
    }

    /**
     * 删除用户(管理员)
     */
    @DeleteMapping(path = "admin/{uid}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer uid) {
        return ResponseEntity.status(userService.deleteUser(uid)).build();
    }
}

package cloud.oj.core.controller;

import cloud.oj.core.entity.User;
import cloud.oj.core.entity.UserFilter;
import cloud.oj.core.entity.UserStatistics;
import cloud.oj.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<UserStatistics> getOverview(Integer uid, Integer year, String tz) {
        return ResponseEntity.ok(userService.getOverview(uid, year, tz));
    }

    /**
     * 根据过滤条件获取用户
     */
    @RequestMapping(path = "admin/queries", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer limit,
            @RequestBody(required = false) UserFilter filter
    ) {
        var data = userService.getUsersByFilter(filter, page, limit);
        return data.getTotal() > 0 ?
                ResponseEntity.ok(data) :
                ResponseEntity.noContent().build();
    }

    /**
     * 获取用户个人信息
     */
    @GetMapping(path = "profile")
    public ResponseEntity<User> getUserInfo(@RequestHeader(name = "uid", required = false) Integer hUid,
                                            @RequestParam Integer uid) {
        var isSelf = uid.equals(hUid);
        return ResponseEntity.ok(userService.getUserInfo(uid, isSelf));
    }

    @GetMapping(path = "admin/profile")
    public ResponseEntity<User> getUserInfoAdmin(@RequestParam Integer uid) {
        return ResponseEntity.ok(userService.getUserInfo(uid, true));
    }

    /**
     * 添加/注册用户
     */
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新用户(管理员)
     */
    @PutMapping(path = "admin")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新用户(个人)
     */
    @PutMapping(path = "profile")
    public ResponseEntity<?> updateProfile(@RequestHeader Integer uid, @RequestBody User user) {
        user.setUid(uid);
        userService.updateProfile(user);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除用户(管理员)
     */
    @DeleteMapping(path = "admin/{uid}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer uid) {
        userService.deleteUser(uid);
        return ResponseEntity.noContent().build();
    }
}

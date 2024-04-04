package cloud.oj.core.controller;

import cloud.oj.core.entity.User;
import cloud.oj.core.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取统计信息
     *
     * @param tz 时区
     * @return {@link java.util.HashMap}
     */
    @GetMapping(path = "overview")
    public ResponseEntity<?> getOverview(Integer uid, Integer year, String tz) {
        return ResponseEntity.ok(userService.getOverview(uid, year, tz));
    }

    /**
     * 根据过滤条件获取用户
     *
     * @param filter      1: by username, 2: by nickname
     * @param filterValue Value of filter
     * @return 用户分页数据
     */
    @GetMapping(path = "admin")
    public Mono<ResponseEntity<?>> getUsers(Integer filter,
                                            String filterValue,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "15") Integer limit) {
        return userService.getUsersByFilter(filter, filterValue, page, limit)
                .flatMap(data -> data.getTotal() > 0 ?
                        Mono.just(ResponseEntity.ok(data))
                        : Mono.just(ResponseEntity.noContent().build())
                );
    }

    /**
     * 获取用户的个人信息
     *
     * @return {@link User}
     */
    @GetMapping(path = "profile")
    public Mono<ResponseEntity<User>> getUserInfo(Integer uid) {
        return userService.getUserInfo(uid)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * 添加/注册用户
     */
    @PostMapping
    public Mono<ResponseEntity<?>> addUser(@RequestBody User user) {
        return userService.addUser(user)
                .flatMap(status -> Mono.just(ResponseEntity.status(status).build()));
    }

    /**
     * 更新用户(管理员)
     */
    @PutMapping(path = "admin")
    public Mono<ResponseEntity<?>> updateUser(@RequestBody User user) {
        return userService.updateUser(user)
                .flatMap(status -> Mono.just(ResponseEntity.status(status).build()));
    }

    /**
     * 更新用户(个人)
     */
    @PutMapping(path = "profile")
    public Mono<ResponseEntity<?>> updateProfile(@RequestHeader Integer uid, @RequestBody User user) {
        return userService.updateProfile(uid, user)
                .flatMap(status -> Mono.just(ResponseEntity.status(status).build()));
    }

    /**
     * 删除用户(管理员)
     */
    @DeleteMapping(path = "admin/{uid}")
    public Mono<ResponseEntity<?>> deleteUser(@PathVariable Integer uid) {
        return userService.deleteUser(uid)
                .flatMap(status -> Mono.just(ResponseEntity.status(status).build()));
    }
}

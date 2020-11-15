package group._204.oj.manager.controller;

import group._204.oj.manager.model.Msg;
import group._204.oj.manager.model.PagedResult;
import group._204.oj.manager.model.User;
import group._204.oj.manager.service.OverviewService;
import group._204.oj.manager.service.UserService;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;

@RestController
@RequestMapping("user")
@Api(tags = "用户")
public class UserController implements CRUDController {

    @Resource
    private UserService userService;

    @Resource
    private OverviewService overviewService;

    @ApiOperation(value = "获取统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true),
            @ApiImplicitParam(name = "year", value = "年份", required = true, example = "2020")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = HashMap.class)
    })
    @GetMapping(path = "overview", produces = "application/json")
    public ResponseEntity<?> getOverview(String userId, Integer year) {
        return buildGETResponse(overviewService.getOverview(userId, year));
    }

    @ApiOperation(value = "获取所有用户", notes = "需要用户管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", required = true, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页数量", required = true, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "pro", produces = "application/json")
    public ResponseEntity<?> getUsers(Integer page, Integer limit) {
        return buildGETResponse(userService.getUsers(page, limit));
    }

    @ApiOperation(value = "添加/注册用户", notes = "密码 = bcrypt(md5(明文密码))")
    @ApiImplicitParam(name = "user", required = true)
    @ApiResponses({
            @ApiResponse(code = 201, message = "添加/注册成功")
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return buildPOSTResponse(userService.addUser(user));
    }

    @ApiOperation(value = "更新用户", notes = "需要用户管理员权限")
    @ApiImplicitParam(name = "user", required = true)
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功")
    })
    @PutMapping(path = "pro", consumes = "application/json")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        return buildPUTResponse(userService.updateUser(user));
    }

    @ApiOperation(value = "更新用户(个人)", notes = "需要用户管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true),
            @ApiImplicitParam(name = "user", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功")
    })
    @PutMapping(path = "profile", consumes = "application/json")
    public ResponseEntity<?> updateProfile(@RequestHeader String userId,
                                           @RequestBody User user) {
        return buildPUTResponse(userService.updateProfile(userId, user));
    }

    @ApiOperation(value = "删除用户", notes = "需要用户管理员权限")
    @ApiImplicitParam(name = "userId", required = true)
    @ApiResponses({
            @ApiResponse(code = 204, message = "删除成功"),
            @ApiResponse(code = 409, message = "无法删除，存在约束", response = Msg.class),
            @ApiResponse(code = 410, message = "用户已经不存在")
    })
    @DeleteMapping(path = "pro/{userId}", produces = "application/json")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        return buildDELETEResponse(userService.deleteUser(userId));
    }
}

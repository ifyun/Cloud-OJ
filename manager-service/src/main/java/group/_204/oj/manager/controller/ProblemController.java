package group._204.oj.manager.controller;

import group._204.oj.manager.model.Msg;
import group._204.oj.manager.model.PagedResult;
import group._204.oj.manager.model.Problem;
import group._204.oj.manager.service.ProblemService;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 题目管理接口
 */
@RestController
@RequestMapping("problem")
@Api(tags = "题目")
public class ProblemController implements CRUDController {

    @Resource
    private ProblemService problemService;

    @ApiOperation(value = "获取所有开放的题目", notes = "若 userId != null，可同时获取每道题目的判题结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户 ID", dataTypeClass = String.class),
            @ApiImplicitParam(name = "keyword", value = "题目关键字", dataTypeClass = String.class),
            @ApiImplicitParam(name = "page", value = "页数", dataTypeClass = Integer.class,
                    required = true, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页的数量", dataTypeClass = Integer.class,
                    required = true, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllEnable(String userId, String keyword,
                                          Integer page, Integer limit) {
        return keyword != null ?
                buildGETResponse(problemService.searchProblems(userId, keyword, page, limit, true))
                : buildGETResponse(problemService.getEnableProblems(userId, page, limit));
    }

    @ApiOperation(value = "获取所有题目", notes = "需要题目管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "题目关键字", dataTypeClass = String.class),
            @ApiImplicitParam(name = "page", value = "页数", dataTypeClass = Integer.class,
                    required = true, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页的数量", dataTypeClass = Integer.class,
                    required = true, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "pro", produces = "application/json")
    public ResponseEntity<?> getAll(String keyword, Integer page, Integer limit) {
        return keyword != null ?
                buildGETResponse(problemService.searchProblems(null, keyword, page, limit, false))
                : buildGETResponse(problemService.getProblems(null, page, limit));
    }

    @ApiOperation(value = "获取单个题目", notes = "获取开放的题目或竞赛中的题目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problemId", value = "题目 ID", dataTypeClass = Integer.class,
                    required = true, example = "1001"),
            @ApiImplicitParam(name = "contestId", value = "竞赛/作业 ID", dataTypeClass = Integer.class,
                    required = true, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = Problem.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "{problemId}", produces = "application/json")
    public ResponseEntity<?> getEnable(@PathVariable Integer problemId, Integer contestId) {
        return contestId == null ?
                buildGETResponse(problemService.getEnableProblem(problemId))
                : buildGETResponse(problemService.getProblem(problemId));
    }

    @ApiOperation(value = "获取单个题目，无论是否开放", notes = "需要题目管理员权限")
    @ApiImplicitParam(name = "problemId", value = "题目 ID", dataTypeClass = Integer.class,
            required = true, example = "1001")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = Problem.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "pro/{problemId}", produces = "application/json")
    public ResponseEntity<?> getSingle(@PathVariable Integer problemId) {
        return buildGETResponse(problemService.getProblem(problemId));
    }

    @ApiOperation(value = "更新题目", notes = "需要题目管理员权限")
    @ApiImplicitParam(name = "problem", value = "题目", dataTypeClass = Problem.class, required = true)
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功"),
            @ApiResponse(code = 400, message = "无法更新", response = Msg.class)
    })
    @PutMapping(path = "pro", consumes = "application/json")
    public ResponseEntity<?> update(@RequestBody Problem problem) {
        return buildPUTResponse(problemService.updateProblem(problem));
    }

    @ApiOperation(value = "切换题目的开放/关闭状态", notes = "需要题目管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problemId", value = "题目 ID", dataTypeClass = Integer.class,
                    required = true, example = "1001"),
            @ApiImplicitParam(name = "enable", value = "是否开放", dataTypeClass = Boolean.class,
                    required = true, example = "true")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功")
    })
    @PutMapping(path = "pro/{problemId}", produces = "application/json")
    public ResponseEntity<?> updateEnable(@PathVariable Integer problemId, Boolean enable) {
        return buildPUTResponse(problemService.toggleEnable(problemId, enable));
    }

    @ApiOperation(value = "添加题目", notes = "需要题目管理员权限")
    @ApiImplicitParam(name = "problem", dataTypeClass = Problem.class, required = true)
    @ApiResponses({
            @ApiResponse(code = 201, message = "添加成功")
    })
    @PostMapping(path = "pro", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> add(@RequestBody Problem problem) {
        return buildPOSTResponse(problemService.addProblem(problem));
    }

    @ApiOperation(value = "删除题目", notes = "需要管理员权限")
    @ApiImplicitParam(name = "problemId", value = "题目 ID", dataTypeClass = Integer.class,
            required = true, example = "1001")
    @ApiResponses({
            @ApiResponse(code = 204, message = "删除成功"),
            @ApiResponse(code = 409, message = "无法删除，存在约束", response = Msg.class),
            @ApiResponse(code = 410, message = "题目已经不存在")
    })
    @DeleteMapping(path = "pro/{problemId}", produces = "application/json")
    public ResponseEntity<?> delete(@PathVariable Integer problemId) {
        return buildDELETEResponse(problemService.deleteProblem(problemId));
    }
}

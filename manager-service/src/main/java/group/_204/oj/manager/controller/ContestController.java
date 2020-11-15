package group._204.oj.manager.controller;

import group._204.oj.manager.model.Msg;
import group._204.oj.manager.model.PagedResult;
import group._204.oj.manager.model.Problem;
import group._204.oj.manager.service.ContestService;
import group._204.oj.manager.service.SystemSettings;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group._204.oj.manager.model.Contest;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("contest")
@Api(tags = "竞赛/作业")
public class ContestController implements CRUDController {

    @Resource
    private ContestService contestService;

    @Resource
    private SystemSettings systemSettings;

    @ApiOperation(value = "获取竞赛/作业", notes = "默认设置下，仅返回已开始的竞赛/作业")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", required = true, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页数量", required = true, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> Contests(Integer page, Integer limit) {
        if (systemSettings.getConfig().isShowNotStartedContest())
            return buildGETResponse(contestService.getAllContest(page, limit));
        else
            return buildGETResponse(contestService.getStartedContest(page, limit));
    }

    @ApiOperation(value = "获取所有竞赛/作业", notes = "需要题目管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", required = true, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页数量", required = true, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "pro", produces = "application/json")
    public ResponseEntity<?> allContests(Integer page, Integer limit) {
        return buildGETResponse(contestService.getAllContest(page, limit));
    }

    @ApiOperation(value = "获取竞赛/作业允许使用的语言")
    @ApiImplicitParam(name = "contestId", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = Contest.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "lang/{contestId}", produces = "application/json")
    public ResponseEntity<?> getLanguages(@PathVariable Integer contestId) {
        return buildGETResponse(contestService.getLanguages(contestId));
    }

    @ApiOperation(value = "从已开始的竞赛/作业中获取题目", notes = "若传入 userId，可以同时获取判题结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contestId", required = true, example = "1"),
            @ApiImplicitParam(name = "userId"),
            @ApiImplicitParam(name = "page", value = "页数", required = true, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页的数量", required = true, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "problem", produces = "application/json")
    public ResponseEntity<?> getProblemsFromStartedContest(Integer contestId, String userId,
                                                           Integer page, Integer limit) {
        return buildGETResponse(contestService.getProblemsFromContest(userId, contestId, true, page, limit));
    }

    @ApiOperation(value = "从已开始的竞赛/作业中获取题目的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contestId", required = true, example = "1"),
            @ApiImplicitParam(name = "problemId", required = true, example = "1001")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = Problem.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "problem/{contestId}/{problemId}")
    public ResponseEntity<?> getProblemInContest(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return buildGETResponse(contestService.getProblemInContest(contestId, problemId));
    }

    @ApiOperation(value = "从竞赛/作业中获取题目", notes = "需要题目管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contestId", required = true, example = "1"),
            @ApiImplicitParam(name = "page", value = "页数", required = true, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页数量", required = true, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "pro/problem", produces = "application/json")
    public ResponseEntity<?> getProblemsFromContest(Integer contestId, Integer page, Integer limit) {
        return buildGETResponse(contestService.getProblemsFromContest(null, contestId, false,
                page, limit));
    }

    @ApiOperation(value = "获取不在竞赛/作业中的题目", notes = "需要题目管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contestId", required = true, example = "1"),
            @ApiImplicitParam(name = "page", value = "页数", required = true, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页数量", required = true, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping("pro/{contestId}")
    public ResponseEntity<?> getProblemsNotInContest(@PathVariable Integer contestId, Integer page, Integer limit) {
        return buildGETResponse(contestService.getProblemsNotInContest(contestId, page, limit));
    }

    @ApiOperation(value = "创建竞赛/作业", notes = "需要题目管理员权限")
    @ApiImplicitParam(name = "contest", value = "竞赛/作业", required = true)
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = Msg.class),
    })
    @PostMapping(path = "pro", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addContest(@RequestBody Contest contest) {
        return buildPOSTResponse(contestService.addContest(contest));
    }

    @ApiOperation(value = "更新竞赛/作业", notes = "需要题目管理员权限")
    @ApiImplicitParam(name = "contest", value = "竞赛/作业", required = true)
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功", response = Msg.class),
    })
    @PutMapping(path = "pro", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateContest(@RequestBody Contest contest) {
        return buildPUTResponse(contestService.updateContest(contest));
    }

    @ApiOperation(value = "删除竞赛/作业", notes = "需要题目管理员权限")
    @ApiImplicitParam(name = "contestId", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(code = 204, message = "删除成功", response = Msg.class),
            @ApiResponse(code = 409, message = "无法删除，存在约束", response = Msg.class),
            @ApiResponse(code = 410, message = "数据已经不存在", response = Msg.class)
    })
    @DeleteMapping(path = "pro/{contestId}", produces = "application/json")
    public ResponseEntity<?> deleteContest(@PathVariable Integer contestId) {
        return buildDELETEResponse(contestService.deleteContest(contestId));
    }

    @ApiOperation(value = "向竞赛/作业添加题目", notes = "需要题目管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contestId", required = true, example = "1"),
            @ApiImplicitParam(name = "problemId", required = true, example = "1001")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "添加成功", response = Msg.class)
    })
    @PostMapping(path = "pro/problem/{contestId}/{problemId}", produces = "application/json")
    public ResponseEntity<?> addProblem(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return buildPOSTResponse(contestService.addProblemToContest(contestId, problemId));
    }

    @ApiOperation(value = "从竞赛/作业中移除题目", notes = "需要题目管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contestId", required = true, example = "1"),
            @ApiImplicitParam(name = "problemId", required = true, example = "1001")
    })
    @ApiResponses({
            @ApiResponse(code = 204, message = "删除成功", response = Msg.class),
            @ApiResponse(code = 409, message = "无法移除，存在约束", response = Msg.class),
            @ApiResponse(code = 410, message = "数据已经不存在", response = Msg.class)
    })
    @DeleteMapping(path = "pro/problem/{contestId}/{problemId}", produces = "application/json")
    public ResponseEntity<?> deleteProblem(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return buildDELETEResponse(contestService.deleteProblemFromContest(contestId, problemId));
    }
}

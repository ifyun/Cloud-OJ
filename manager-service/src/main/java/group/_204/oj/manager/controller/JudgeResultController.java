package group._204.oj.manager.controller;

import group._204.oj.manager.model.PagedResult;
import group._204.oj.manager.service.CommitHistoryService;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("history")
@Api(tags = "提交记录")
public class JudgeResultController implements CRUDController {

    @Resource
    private CommitHistoryService commitHistoryService;


    @ApiOperation(value = "获取提交记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true),
            @ApiImplicitParam(name = "page", value = "页数", required = true, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页数量", required = true, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getJudged(@RequestHeader String userId, Integer page, Integer limit) {
        return buildGETResponse(commitHistoryService.getJudged(userId, page, limit));
    }
}

package group._204.oj.manager.controller;

import group._204.oj.manager.dao.ContestDao;
import group._204.oj.manager.model.JudgeResult;
import group._204.oj.manager.model.Msg;
import group._204.oj.manager.model.PagedResult;
import group._204.oj.manager.service.RankingService;
import group._204.oj.manager.service.SystemSettings;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("ranking")
@Api(tags = "排行榜")
public class RankingController implements CRUDController {
    @Resource
    private RankingService rankingService;

    @Resource
    private ContestDao contestDao;

    @Resource
    private SystemSettings systemSettings;

    @ApiOperation(value = "获取排行榜", notes = "此排行榜不计算竞赛/作业中的提交")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataTypeClass = Integer.class, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页的数量", dataTypeClass = Integer.class, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getRankingList(Integer page, Integer limit) {
        return buildGETResponse(rankingService.getRanking(page, limit));
    }

    @ApiOperation(value = "获取竞赛排行榜")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contestId", value = "竞赛/作业 ID", dataTypeClass = Integer.class, example = "1"),
            @ApiImplicitParam(name = "page", value = "页数", dataTypeClass = Integer.class, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页的数量", dataTypeClass = Integer.class, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "contest/{contestId}", produces = "application/json")
    public ResponseEntity<?> getContestRanking(@PathVariable Integer contestId, Integer page, Integer limit) {
        if (systemSettings.getConfig().isShowRankingAfterEnded() && !contestDao.isContestEnded(contestId)) {
            return ResponseEntity.status(403).body(new Msg("结束后才可查看"));
        }

        return buildGETResponse(rankingService.getContestRanking(contestId, page, limit));
    }

    @ApiOperation(value = "获取竞赛排行榜(管理员专用)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contestId", value = "竞赛/作业 ID", dataTypeClass = Integer.class, example = "1"),
            @ApiImplicitParam(name = "page", value = "页数", dataTypeClass = Integer.class, example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页的数量", dataTypeClass = Integer.class, example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PagedResult.class),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "pro/contest/{contestId}", produces = "application/json")
    public ResponseEntity<?> getRankingListAdmin(@PathVariable Integer contestId, Integer page, Integer limit) {
        return buildGETResponse(rankingService.getContestRanking(contestId, page, limit));
    }

    @ApiOperation(value = "获取用户的详细得分情况", notes = "需要题目管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contestId", value = "竞赛/作业 ID", dataTypeClass = Integer.class, required = true, example = "1"),
            @ApiImplicitParam(name = "userId", value = "用户 ID", dataTypeClass = String.class, required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = JudgeResult.class, responseContainer = "List"),
            @ApiResponse(code = 204, message = "无数据")
    })
    @GetMapping(path = "pro/contest/detail", produces = "application/json")
    public ResponseEntity<?> getDetail(Integer contestId, String userId) {
        return buildGETResponse(rankingService.getDetail(contestId, userId));
    }
}

package group._204.oj.judge.controller;

import group._204.oj.judge.dao.ContestDao;
import group._204.oj.judge.model.CommitData;
import group._204.oj.judge.model.Contest;
import group._204.oj.judge.model.JudgeResult;
import group._204.oj.judge.model.Msg;
import group._204.oj.judge.service.CommitService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 提交代码接口
 */
@Slf4j
@RestController
@RequestMapping("/commit")
@Api(tags = "提交代码/获取结果")
public class CommitController {

    @Resource
    private CommitService commitService;

    @Resource
    private ContestDao contestDao;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Queue commitQueue;

    @ApiOperation(value = "提交代码")
    @ApiImplicitParam(name = "data", value = "用户的代码及相关数据", dataTypeClass = CommitData.class, required = true)
    @ApiResponses({
            @ApiResponse(code = 202, message = "已接受(提交到队列)"),
            @ApiResponse(code = 400, message = "提交失败，可能使用了不允许的语言", response = Msg.class),
            @ApiResponse(code = 403, message = "不允许提交，竞赛/作业已结束", response = Msg.class)
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> commitCode(@RequestBody CommitData data) {
        Integer contestId = data.getContestId();

        if (contestId != null) {
            Contest contest = contestDao.getContest(contestId);
            if (contest.isEnded())
                return ResponseEntity.status(403).body(new Msg("当前竞赛/作业已结束"));
            int lang = data.getLanguage();
            int languages = contest.getLanguages();
            if ((languages & 1 << lang) != 1 << lang) {
                return ResponseEntity.badRequest().body(new Msg("不允许使用该语言"));
            }
        }

        data.setSolutionId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(commitQueue.getName(), data);

        return ResponseEntity.accepted().body(data.getSolutionId());
    }

    /**
     * 获取判题结果
     *
     * @param solutionId 题目 Id
     * @return {@link ResponseEntity} 判题结果
     */
    @ApiOperation(value = "获取结果")
    @ApiImplicitParam(name = "solutionId", value = "答案 UUID", dataTypeClass = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取结果", response = JudgeResult.class),
            @ApiResponse(code = 204, message = "无结果")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getResult(String solutionId) {
        JudgeResult result = commitService.getResult(solutionId);
        return result == null ?
                ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }
}

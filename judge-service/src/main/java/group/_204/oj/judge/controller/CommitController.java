package group._204.oj.judge.controller;

import group._204.oj.judge.dao.ContestDao;
import group._204.oj.judge.model.CommitData;
import group._204.oj.judge.model.Contest;
import group._204.oj.judge.model.JudgeResult;
import group._204.oj.judge.model.Msg;
import group._204.oj.judge.service.CommitService;
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
public class CommitController {

    @Resource
    private CommitService commitService;

    @Resource
    private ContestDao contestDao;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Queue commitQueue;

    /**
     * 提交代码
     */
    @PostMapping()
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
     */
    @GetMapping()
    public ResponseEntity<?> getResult(String solutionId) {
        JudgeResult result = commitService.getResult(solutionId);
        return result == null ?
                ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }
}

package group._204.oj.judge.controller;

import group._204.oj.judge.dao.ContestDao;
import group._204.oj.judge.model.CommitData;
import group._204.oj.judge.model.Msg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * 提交代码接口
 */
@Slf4j
@RestController
@RequestMapping("/commit")
public class CommitController {

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
        var contestId = data.getContestId();

        if (data.getSourceCode().isEmpty()) {
            return ResponseEntity.badRequest().body(new Msg("代码为空"));
        }

        if (contestId != null) {
            var contest = contestDao.getContest(contestId);
            if (contest.isEnded()) {
                return ResponseEntity.status(403).body(new Msg("当前竞赛已结束"));
            }
            var lang = data.getLanguage();
            var languages = contest.getLanguages();

            if ((languages & 1 << lang) != 1 << lang) {
                return ResponseEntity.badRequest().body(new Msg("不允许使用该语言"));
            }
        }

        data.setSolutionId(UUID.randomUUID().toString());
        data.setSubmitTime(new Date());
        rabbitTemplate.convertAndSend(commitQueue.getName(), data);

        return ResponseEntity.accepted().body(data.getSolutionId());
    }
}

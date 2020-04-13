package top.cloudli.judgeservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.judgeservice.dao.ContestDao;
import top.cloudli.judgeservice.data.CommitData;
import top.cloudli.judgeservice.model.Contest;
import top.cloudli.judgeservice.service.CommitService;

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
    private CommitService commitService;

    @Resource
    private ContestDao contestDao;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Queue commitQueue;

    @PostMapping("")
    public ResponseEntity<?> commitCode(@RequestBody CommitData data) {
        Integer contestId = data.getContestId();

        if (contestId != null) {
            Contest contest = contestDao.getContest(contestId);
            if (contest.getEndAt().before(new Date()))
                return ResponseEntity.ok("当前竞赛/作业已结束！");
            int lang = data.getLanguage();
            int languages = contest.getLanguages();
            if ((languages & 1 << lang) != 1 << lang) {
                return ResponseEntity.badRequest().body("不允许使用该语言.");
            }
        }

        data.setSolutionId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(commitQueue.getName(), data);
        return ResponseEntity.accepted().body(data.getSolutionId());
    }

    @GetMapping("")
    public ResponseEntity<?> getResult(String solutionId) {
        return ResponseEntity.ok(commitService.getResult(solutionId));
    }
}

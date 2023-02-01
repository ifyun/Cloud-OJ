package cloud.oj.judge.controller;

import cloud.oj.judge.dao.ContestDao;
import cloud.oj.judge.dao.ProblemDao;
import cloud.oj.judge.entity.CommitData;
import cloud.oj.judge.error.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private ContestDao contestDao;

    @Resource
    private ProblemDao problemDao;

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

        if (data.getSourceCode().trim().isEmpty()) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "代码为空");
        }

        if (contestId != null) {
            var contest = contestDao.getContest(contestId);

            if (contest.isEnded()) {
                throw new GenericException(HttpStatus.BAD_REQUEST.value(), "当前竞赛已结束");
            }

            var lang = data.getLanguage();
            var languages = contest.getLanguages();

            if ((languages & 1 << lang) != 1 << lang) {
                throw new GenericException(HttpStatus.BAD_REQUEST.value(), "不允许使用该语言");
            }
        } else {
            if (!problemDao.isOpen(data.getProblemId())) {
                throw new GenericException(HttpStatus.FORBIDDEN.value(), "未开放，不允许提交");
            }
        }

        data.setSolutionId(UUID.randomUUID().toString());
        data.setSubmitTime(System.currentTimeMillis() / 1000);
        rabbitTemplate.convertAndSend(commitQueue.getName(), data);

        return ResponseEntity.accepted().body(data.getSolutionId());
    }
}

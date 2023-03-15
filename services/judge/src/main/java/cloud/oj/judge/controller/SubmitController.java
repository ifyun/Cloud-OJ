package cloud.oj.judge.controller;

import cloud.oj.judge.config.RabbitConfig;
import cloud.oj.judge.dao.ContestDao;
import cloud.oj.judge.dao.ProblemDao;
import cloud.oj.judge.entity.CommitData;
import cloud.oj.judge.error.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 提交代码接口
 */
@Slf4j
@RestController
public class SubmitController {

    private final ContestDao contestDao;

    private final ProblemDao problemDao;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public SubmitController(ContestDao contestDao, ProblemDao problemDao, RabbitTemplate rabbitTemplate) {
        this.contestDao = contestDao;
        this.problemDao = problemDao;
        this.rabbitTemplate = rabbitTemplate;
    }

    private ResponseEntity<?> submitCode(CommitData data, boolean isAdmin) {
        var contestId = data.getContestId();

        if (data.getSourceCode().trim().isEmpty()) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "代码为空");
        }

        if (contestId != null) {
            var contest = contestDao.getContest(contestId);

            if (contest.isEnded()) {
                throw new GenericException(HttpStatus.BAD_REQUEST.value(), "当前竞赛已结束");
            }

            var lang = 1 << data.getLanguage();
            var languages = contest.getLanguages();

            if ((languages & lang) != lang) {
                throw new GenericException(HttpStatus.BAD_REQUEST.value(), "不允许使用该语言");
            }
        } else {
            if (!isAdmin && !problemDao.isOpen(data.getProblemId())) {
                throw new GenericException(HttpStatus.FORBIDDEN.value(), "未开放，不允许提交");
            }
        }

        data.setSolutionId(UUID.randomUUID().toString());
        data.setSubmitTime(System.currentTimeMillis() / 1000);
        rabbitTemplate.convertAndSend(RabbitConfig.SUBMIT_QUEUE, data);

        return ResponseEntity.accepted().body(data.getSolutionId());
    }

    /**
     * 提交代码，普通用户
     */
    @PostMapping("submit")
    public ResponseEntity<?> submit(@RequestBody CommitData data) {
        return submitCode(data, false);
    }

    /**
     * 提交代码，管理员
     */
    @PostMapping("admin/submit")
    public ResponseEntity<?> adminSubmit(@RequestBody CommitData data) {
        return submitCode(data, true);
    }
}

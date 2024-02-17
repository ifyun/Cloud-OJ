package cloud.oj.judge.service;

import cloud.oj.judge.config.RabbitConfig;
import cloud.oj.judge.dao.*;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.entity.SubmitData;
import cloud.oj.judge.error.GenericException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmitService {

    private final ProblemDao problemDao;

    private final ContestDao contestDao;

    private final InviteeDao inviteeDao;

    private final SolutionDao solutionDao;

    private final SourceDao sourceDao;

    private final RabbitTemplate rabbitTemplate;

    private final Queue judgeQueue;

    /**
     * 提交到队列
     *
     * @param data    {@link SubmitData}
     * @param isAdmin 是否为管理员
     * @return {@link ResponseEntity}
     */
    public ResponseEntity<?> submitCode(SubmitData data, boolean isAdmin) {
        data.setSubmitTime(System.currentTimeMillis());
        var contestId = data.getContestId();

        if (data.getSourceCode().trim().isEmpty()) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "代码为空，不准提交");
        }

        if (contestId != null) {
            // 竞赛提交
            var contest = contestDao.getContest(contestId);

            if (Boolean.FALSE.equals(inviteeDao.checkInvitee(contestId, data.getUid()))) {
                throw new GenericException(HttpStatus.FORBIDDEN, "非邀请用户");
            }

            if (!contest.isStarted()) {
                throw new GenericException(HttpStatus.FORBIDDEN, "未开始，不准提交");
            }

            if (contest.isEnded()) {
                throw new GenericException(HttpStatus.FORBIDDEN, "已结束，不准提交");
            }

            // 检查语言
            var lang = 1 << data.getLanguage();
            var languages = contest.getLanguages();

            if ((languages & lang) != lang) {
                throw new GenericException(HttpStatus.BAD_REQUEST, "不准使用该语言");
            }
        } else {
            // 非竞赛提交
            if (!isAdmin && !problemDao.isEnable(data.getProblemId())) {
                throw new GenericException(HttpStatus.FORBIDDEN, "未开放，不准提交");
            }
        }

        rabbitTemplate.convertAndSend(RabbitConfig.SUBMIT_QUEUE, data);
        return ResponseEntity.accepted().body(data.getSubmitTime());
    }

    /**
     * 保存提交到数据库
     * <p>隔离级别：读提交</p>
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void submit(SubmitData submitData) throws AmqpException {
        var solution = new Solution(
                submitData.getUid(),
                submitData.getProblemId(),
                submitData.getContestId(),
                submitData.getLanguage(),
                submitData.getSubmitTime(),
                submitData.getSourceCode()
        );

        solutionDao.create(solution);
        // solutionId 由数据库生成
        sourceDao.create(solution.getSolutionId(), submitData.getSourceCode());
        // 发送到判题队列
        rabbitTemplate.convertAndSend(judgeQueue.getName(), solution);
    }
}

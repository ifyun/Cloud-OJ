package cloud.oj.judge.service;

import cloud.oj.judge.config.RabbitConfig;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.entity.SubmitData;
import cloud.oj.judge.error.GenericException;
import cloud.oj.judge.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmitService {

    private final ProblemRepo problemRepo;

    private final ContestRepo contestRepo;

    private final InviteeRepo inviteeRepo;

    private final SolutionRepo solutionRepo;

    private final SourceRepo sourceRepo;

    private final RabbitTemplate rabbitTemplate;

    private final Queue judgeQueue;

    /**
     * 提交到队列
     *
     * @param data    {@link SubmitData}
     * @param isAdmin 是否为管理员
     * @return {@link Long} 提交时间(unix timestamp)
     */
    public Long submitCode(SubmitData data, boolean isAdmin) {
        data.setSubmitTime(System.currentTimeMillis());
        var cid = data.getContestId();

        if (data.getSourceCode().trim().isEmpty()) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "代码为空，不准提交");
        }

        if (cid != null) {
            // 竞赛提交
            var contest = contestRepo.selectById(cid);

            if (contest.isEmpty()) {
                throw new GenericException(HttpStatus.NOT_FOUND, "竞赛不存在");
            }

            if (Boolean.FALSE.equals(inviteeRepo.checkInvitee(cid, data.getUid()))) {
                throw new GenericException(HttpStatus.FORBIDDEN, "未加入竞赛");
            }

            if (!contest.get().isStarted()) {
                throw new GenericException(HttpStatus.FORBIDDEN, "未开始，不准提交");
            }

            if (contest.get().isEnded()) {
                throw new GenericException(HttpStatus.FORBIDDEN, "已结束，不准提交");
            }

            // 检查语言
            var lang = 1 << data.getLanguage();
            var languages = contest.get().getLanguages();

            if ((languages & lang) != lang) {
                throw new GenericException(HttpStatus.BAD_REQUEST, "不准使用该语言");
            }
        } else {
            // 非竞赛提交
            if (!isAdmin && !problemRepo.isEnable(data.getProblemId())) {
                throw new GenericException(HttpStatus.FORBIDDEN, "未开放，不准提交");
            }
        }

        rabbitTemplate.convertAndSend(RabbitConfig.SUBMIT_QUEUE, data);
        return data.getSubmitTime();
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
        // solutionId 由数据库生成
        solutionRepo.insert(solution);
        sourceRepo.insert(solution.getId(), submitData.getSourceCode());
        // 发送到判题队列
        rabbitTemplate.convertAndSend(judgeQueue.getName(), solution);
    }
}

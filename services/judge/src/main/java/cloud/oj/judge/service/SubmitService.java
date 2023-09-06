package cloud.oj.judge.service;

import cloud.oj.judge.component.SolutionState;
import cloud.oj.judge.config.RabbitConfig;
import cloud.oj.judge.dao.ContestDao;
import cloud.oj.judge.dao.ProblemDao;
import cloud.oj.judge.dao.SolutionDao;
import cloud.oj.judge.dao.SourceCodeDao;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.entity.SourceCode;
import cloud.oj.judge.entity.SubmitData;
import cloud.oj.judge.error.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class SubmitService {

    private final ProblemDao problemDao;

    private final ContestDao contestDao;

    private final SolutionDao solutionDao;

    private final SourceCodeDao sourceCodeDao;

    private final RabbitTemplate rabbitTemplate;

    private final Queue judgeQueue;

    public SubmitService(ProblemDao problemDao, ContestDao contestDao, SolutionDao solutionDao,
                         SourceCodeDao sourceCodeDao, RabbitTemplate rabbitTemplate, Queue judgeQueue) {
        this.problemDao = problemDao;
        this.contestDao = contestDao;
        this.solutionDao = solutionDao;
        this.sourceCodeDao = sourceCodeDao;
        this.rabbitTemplate = rabbitTemplate;
        this.judgeQueue = judgeQueue;
    }

    /**
     * 提交到队列
     *
     * @param data    {@link SubmitData}
     * @param isAdmin 是否为管理员
     * @return {@link ResponseEntity}
     */
    public ResponseEntity<?> submitCode(SubmitData data, boolean isAdmin) {
        var contestId = data.getContestId();
        var status = HttpStatus.BAD_REQUEST;

        if (data.getSourceCode().trim().isEmpty()) {
            throw new GenericException(status, "一行代码都没有，不准提交");
        }

        if (contestId != null) {
            var contest = contestDao.getContest(contestId);

            if (contest.isStarted()) {
                throw new GenericException(status, "未开始，不准提交");
            }

            if (contest.isEnded()) {
                throw new GenericException(status, "已结束，不准提交");
            }

            var lang = 1 << data.getLanguage();
            var languages = contest.getLanguages();

            if ((languages & lang) != lang) {
                throw new GenericException(status, "不准使用该语言");
            }
        } else {
            if (!isAdmin && !problemDao.isEnable(data.getProblemId())) {
                throw new GenericException(status, "未开放，不准提交");
            }
        }

        data.setSolutionId(UUID.randomUUID().toString());
        data.setSubmitTime(System.currentTimeMillis());
        rabbitTemplate.convertAndSend(RabbitConfig.SUBMIT_QUEUE, data);

        return ResponseEntity.accepted().body(data.getSolutionId());
    }

    /**
     * 保存提交到数据库
     * <p>隔离级别：读提交</p>
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void submit(SubmitData submitData) throws AmqpException {
        var solution = new Solution(
                submitData.getSolutionId(),
                submitData.getUid(),
                submitData.getProblemId(),
                submitData.getContestId(),
                submitData.getLanguage(),
                submitData.getSubmitTime()
        );

        var sourceCode = new SourceCode(solution.getSolutionId(), submitData.getSourceCode());

        solutionDao.add(solution);
        sourceCodeDao.add(sourceCode);

        solution.setSourceCode(submitData.getSourceCode());
        solution.setState(SolutionState.IN_QUEUE);
        solutionDao.update(solution);
        // 发送到判题队列
        rabbitTemplate.convertAndSend(judgeQueue.getName(), solution);
    }
}

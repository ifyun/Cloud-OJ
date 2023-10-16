package cloud.oj.core.service;

import cloud.oj.core.dao.ContestDao;
import cloud.oj.core.dao.InviteeDao;
import cloud.oj.core.dao.ProblemDao;
import cloud.oj.core.dao.SolutionDao;
import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.error.GenericException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final InviteeDao inviteeDao;

    private final ContestDao contestDao;

    private final ProblemDao problemDao;

    private final SolutionDao solutionDao;

    /**
     * 为竞赛生成新邀请码
     */
    public String newInviteKey(Integer contestId) {
        var key = RandomStringUtils.randomNumeric(6);
        contestDao.newInviteKey(contestId, key);
        return key;
    }

    /**
     * 根据用户输入的邀请码将用户加入竞赛
     */
    public ResponseEntity<?> inviteUserWithKey(Integer contestId, Integer uid, String key) {
        if (!key.equals(contestDao.getInviteKey(contestId))) {
            throw new GenericException(HttpStatus.NOT_ACCEPTABLE, "Invalid Invite Key");
        }

        if (inviteeDao.checkInvitee(contestId, uid) == null) {
            inviteeDao.inviteUser(contestId, uid);
        }

        return ResponseEntity.ok().build();
    }

    public List<List<?>> getAllContest(int page, int limit) {
        return contestDao.getAll(false,(page - 1) * limit, limit);
    }

    public List<List<?>> getAllContestAdmin(int page, int limit) {
        return contestDao.getAll(true,(page - 1) * limit, limit);
    }

    public Optional<Contest> getContest(Integer contestId) {
        return Optional.ofNullable(contestDao.getContestById(contestId));
    }

    public List<List<?>> getStartedContest(int page, int limit) {
        return contestDao.getStarted((page - 1) * limit, limit);
    }

    public HttpStatus create(Contest contest) {
        contest.setInviteKey(RandomStringUtils.randomNumeric(6));

        if (contestDao.createContest(contest) == 1) {
            return HttpStatus.CREATED;
        } else {
            throw new GenericException(HttpStatus.BAD_REQUEST, "请求数据可能不正确");
        }
    }

    public HttpStatus updateContest(Contest contest) {
        if (contestDao.updateContest(contest) == 1) {
            return HttpStatus.OK;
        } else {
            var msg = String.format("竞赛(%d)更新失败", contest.getContestId());
            throw new GenericException(HttpStatus.BAD_REQUEST, msg);
        }
    }

    /**
     * 逻辑删除竞赛
     * <p>已开始未结束的竞赛不允许删除</p>
     *
     * @param contestId 竞赛 Id
     * @return HTTP 状态码 {@link HttpStatus}
     */
    public HttpStatus deleteContest(Integer contestId) {
        var contest = contestDao.getState(contestId);

        if (contest.isStarted() && !contest.isEnded()) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "竞赛已开始，不准删除");
        }

        if (contestDao.deleteContest(contestId) < 1) {
            throw new GenericException(HttpStatus.GONE, String.format("竞赛(%d)不存在", contestId));
        }

        return HttpStatus.NO_CONTENT;
    }

    public List<List<?>> getProblemsNotInContest(Integer contestId, int page, int limit) {
        return contestDao.getProblemsNotInContest(contestId, (page - 1) * limit, limit);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Problem> getProblemsFromContest(Integer uid, Integer contestId, boolean admin) {
        if (admin) {
            // 管理员查询
            return contestDao.getProblems(contestId);
        }

        if (inviteeDao.checkInvitee(contestId, uid) == null) {
            // 用户没有被邀请，返回 402
            throw new GenericException(HttpStatus.PAYMENT_REQUIRED, "Invite Key Required");
        }

        var problems = contestDao.getProblemsInStarted(contestId);
        // 查询每一题的判题结果
        problems.forEach((p) -> p.setResult(solutionDao.getResultOfContest(uid, contestId, p.getProblemId())));

        return problems;
    }

    public Optional<Problem> getProblemInContest(Integer contestId, Integer problemId) {
        return Optional.ofNullable(problemDao.getByIdFromContest(contestId, problemId));
    }

    public HttpStatus addProblemToContest(Integer contestId, Integer problemId) {
        if (contestDao.addProblem(contestId, problemId) == 1) {
            return HttpStatus.CREATED;
        } else {
            throw new GenericException(HttpStatus.BAD_REQUEST, "无法添加题目");
        }
    }

    public HttpStatus removeProblem(Integer contestId, Integer problemId) {
        if (contestDao.removeProblem(contestId, problemId) == 1) {
            return HttpStatus.NO_CONTENT;
        } else {
            throw new GenericException(HttpStatus.GONE, String.format("题目(%d)不存在", problemId));
        }
    }
}

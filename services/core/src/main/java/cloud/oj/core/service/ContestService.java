package cloud.oj.core.service;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.ContestFilter;
import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.error.GenericException;
import cloud.oj.core.repo.CommonRepo;
import cloud.oj.core.repo.ContestRepo;
import cloud.oj.core.repo.InviteeRepo;
import cloud.oj.core.repo.SolutionRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final CommonRepo commonRepo;

    private final InviteeRepo inviteeRepo;

    private final ContestRepo contestRepo;

    private final SolutionRepo solutionRepo;

    private final SystemSettings systemSettings;

    /**
     * 为竞赛生成新邀请码
     */
    public String newInviteKey(Integer cid) {
        var key = RandomStringUtils.randomNumeric(6);

        if (contestRepo.newInviteKey(cid, key) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "竞赛不存在");
        }

        return key;
    }

    /**
     * 将用户加入竞赛
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void inviteUserWithKey(Integer cid, Integer uid, String key) {
        var inviteKey = contestRepo.selectInviteKey(cid);

        if (inviteKey.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "竞赛不存在");
        }

        if (!inviteKey.get().equals(key)) {
            throw new GenericException(HttpStatus.NOT_ACCEPTABLE, "邀请码错误");
        }

        if (Boolean.FALSE.equals(inviteeRepo.isInvited(cid, uid)) && inviteeRepo.invite(cid, uid) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "加入竞赛失败");
        }
    }

    public Contest getContest(Integer cid) {
        var contest = contestRepo.selectById(cid);

        if (contest.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "竞赛不存在");
        }

        return contest.get();
    }

    /**
     * 分页查询所有竞赛(User)
     *
     * @param filter 过滤条件
     * @param page   页数
     * @param size   每页数量
     * @return {@link PageData} of {@link Contest}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PageData<Contest> getAllContest(ContestFilter filter, Integer page, Integer size) {
        List<Contest> data;

        if (systemSettings.getSettings().isShowAllContest()) {
            data = contestRepo.selectAll(filter, page, size, false);
        } else {
            data = contestRepo.selectAllStarted(filter, page, size);
        }

        var total = commonRepo.selectFoundRows();

        return new PageData<>(data, total);
    }

    /**
     * 分页查询所有竞赛(Admin)
     *
     * @param filter 过滤条件
     * @param page   页数
     * @param size   每页数量
     * @return {@link PageData} of {@link Contest}
     */
    @Transactional
    public PageData<Contest> getAllContestAdmin(ContestFilter filter, Integer page, Integer size) {
        if (filter == null) {
            filter = new ContestFilter();
        }

        var data = contestRepo.selectAll(filter, page, size, true);
        var total = commonRepo.selectFoundRows();

        return new PageData<>(data, total);
    }

    /**
     * 创建竞赛
     *
     * @param contest 竞赛
     */
    public void create(Contest contest) {
        contest.setInviteKey(RandomStringUtils.randomNumeric(6));

        if (contestRepo.insert(contest) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "创建失败");
        }
    }

    /**
     * 修改竞赛
     *
     * @param contest 竞赛
     */
    public void updateContest(Contest contest) {
        if (contestRepo.update(contest) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "更新失败");
        }
    }

    /**
     * 逻辑删除竞赛
     *
     * @param cid 竞赛 Id
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteContest(Integer cid) {
        var contest = contestRepo.selectById(cid);

        if (contest.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "竞赛不存在");
        }

        if (contest.get().isStarted() && !contest.get().isEnded()) {
            // 已开始未结束的竞赛不允许删除
            throw new GenericException(HttpStatus.BAD_REQUEST, "竞赛已开始，不准删除");
        }

        if (contestRepo.delete(cid) == 0) {
            throw new GenericException(HttpStatus.GONE, "竞赛不存在");
        }
    }

    /**
     * 改变竞赛中题目的顺序
     *
     * @param cid      竞赛 Id
     * @param problems {@link List} of 题目 Id
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void changeOrders(Integer cid, List<Integer> problems) {
        var order = new AtomicInteger(0);
        // 按前端传回的顺序设置 order
        problems.forEach(pid -> {
            if (contestRepo.updateOrder(cid, pid, order.getAndAdd(1)) == 0) {
                throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, "更新失败");
            }
        });
    }

    /**
     * 分页查询可用题目
     *
     * @param cid  竞赛 Id
     * @param page 页数
     * @param size 每页数量
     * @return {@link PageData} of {@link Problem}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PageData<Problem> getIdleProblems(Integer cid, Integer page, Integer size) {
        var data = contestRepo.selectIdleProblems(cid, page, size);
        var total = commonRepo.selectFoundRows();

        return new PageData<>(data, total);
    }

    /**
     * 查询竞赛中的题目
     *
     * @param uid   用户 Id
     * @param cid   竞赛 Id
     * @param admin 是否为管理员
     * @return {@link List} of {@link Problem}
     */
    @Transactional
    public List<Problem> getProblemsOfContest(Integer uid, Integer cid, boolean admin) {
        if (admin) {
            // 管理员查询
            return contestRepo.selectProblems(cid, false);
        }

        if (inviteeRepo.isInvited(cid, uid).equals(Boolean.FALSE)) {
            // 用户没有被邀请，返回 402
            throw new GenericException(HttpStatus.PAYMENT_REQUIRED, "请使用邀请码加入竞赛");
        }

        // 普通用户查询，同时返回每题结果
        var problems = contestRepo.selectProblems(cid, true);

        problems.forEach(p -> {
            var result = solutionRepo.selectResultOfContest(uid, cid, p.getProblemId());
            result.ifPresent(p::setResult);
        });

        return problems;
    }

    /**
     * 查询竞赛中的题目详情
     *
     * @param uid 用户 Id
     * @param cid 竞赛 Id
     * @param pid 题目 Id
     * @return {@link Problem}
     */
    public Problem getProblem(Integer uid, Integer cid, Integer pid) {
        if (inviteeRepo.isInvited(cid, uid).equals(Boolean.FALSE)) {
            throw new GenericException(HttpStatus.PAYMENT_REQUIRED, "未加入竞赛");
        }

        var problem = contestRepo.selectProblem(cid, pid);

        if (problem.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "找不到题目");
        }

        return problem.get();
    }

    /**
     * 向竞赛添加题目
     *
     * @param cid 竞赛 Id
     * @param pid 题目 Id
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void addProblemToContest(Integer cid, Integer pid) {
        if (contestRepo.countProblems(cid) == 13) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "不能超过 13 题");
        }

        if (contestRepo.addProblem(cid, pid) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "无法添加题目");
        }
    }

    /**
     * 从竞赛中移除题目
     *
     * @param cid 竞赛 Id
     * @param pid 题目 Id
     */
    public void removeProblem(Integer cid, Integer pid) {
        if (contestRepo.removeProblem(cid, pid) == 0) {
            throw new GenericException(HttpStatus.GONE, "题目不存在");
        }
    }
}

package cloud.oj.core.service;

import cloud.oj.core.entity.Contest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final CommonRepo commonRepo;

    private final InviteeRepo inviteeRepo;

    private final ContestRepo contestRepo;

    private final SolutionRepo solutionRepo;

    /**
     * 为竞赛生成新邀请码
     */
    public Mono<String> newInviteKey(Integer cid) {
        var key = RandomStringUtils.randomNumeric(6);
        return contestRepo.newInviteKey(cid, key)
                .flatMap(rows -> rows > 0 ?
                        Mono.just(key) :
                        Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "竞赛不存在"))
                );
    }

    /**
     * 将用户加入竞赛
     */
    public Mono<ResponseEntity<?>> inviteUserWithKey(Integer cid, Integer uid, String key) {
        return contestRepo.selectInviteKey(cid)
                .flatMap(realKey -> key.equals(realKey) ?
                        Mono.empty() :
                        Mono.error(new GenericException(HttpStatus.NOT_ACCEPTABLE, "邀请码错误"))
                )
                .then(inviteeRepo.isInvited(cid, uid))
                .flatMap(isInvited -> Boolean.FALSE.equals(isInvited) ?
                        inviteeRepo.invite(cid, uid) :
                        Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "已加入竞赛"))
                )
                .flatMap(rows -> rows > 0 ?
                        Mono.just(ResponseEntity.ok().build())
                        : Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "无法加入"))
                );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<PageData<Contest>> getAllContest(Integer page, Integer size) {
        return contestRepo.selectAll(page, size, false)
                .collectList()
                .zipWith(commonRepo.selectFoundRows())
                .map(t -> new PageData<>(t.getT1(), t.getT2()));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<PageData<Contest>> getAllContestAdmin(Integer page, Integer size) {
        return contestRepo.selectAll(page, size, true)
                .collectList()
                .zipWith(commonRepo.selectFoundRows())
                .map(t -> new PageData<>(t.getT1(), t.getT2()));
    }

    public Mono<Contest> getContest(Integer cid) {
        return contestRepo.selectById(cid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<PageData<Contest>> getStartedContest(Integer page, Integer size) {
        return contestRepo.selectAllStarted(page, size)
                .collectList()
                .zipWith(commonRepo.selectFoundRows())
                .map(t -> new PageData<>(t.getT1(), t.getT2()));
    }

    public Mono<HttpStatus> create(Contest contest) {
        contest.setInviteKey(RandomStringUtils.randomNumeric(6));
        return contestRepo.insert(contest)
                .flatMap(rows -> rows > 0 ?
                        Mono.just(HttpStatus.CREATED) :
                        Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "创建失败"))
                );
    }

    public Mono<HttpStatus> updateContest(Contest contest) {
        return contestRepo.update(contest)
                .flatMap(rows -> rows > 0 ?
                        Mono.just(HttpStatus.OK) :
                        Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "更新失败"))
                );
    }

    /**
     * 逻辑删除竞赛
     *
     * @param cid 竞赛 Id
     * @return HTTP 状态码 {@link HttpStatus}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<HttpStatus> deleteContest(Integer cid) {
        return contestRepo.selectById(cid)
                .flatMap(contest -> {
                    if (contest.isStarted() && !contest.isEnded()) {
                        // 已开始未结束的竞赛不允许删除
                        return Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "竞赛已开始，不准删除"));
                    }

                    return contestRepo.delete(cid);
                })
                .flatMap(rows -> rows > 0 ?
                        Mono.just(HttpStatus.NO_CONTENT) :
                        Mono.error(new GenericException(HttpStatus.GONE, "竞赛不存在"))
                );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<Void> changeOrders(Integer cid, List<Integer> problems) {
        var order = new AtomicInteger();
        // 按前端传回的顺序设置 order
        var queries = problems.stream()
                .map(pid -> contestRepo.updateOrder(cid, pid, order.getAndAdd(1)))
                .toList();

        return Flux.fromIterable(queries)
                .flatMapSequential(Function.identity(), 1)
                .then();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<PageData<Problem>> getIdleProblems(Integer cid, Integer page, Integer size) {
        return contestRepo.selectIdleProblems(cid, page, size)
                .collectList()
                .zipWith(commonRepo.selectFoundRows())
                .map(t -> new PageData<>(t.getT1(), t.getT2()));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<List<Problem>> getProblemsOfContest(Integer uid, Integer cid, boolean admin) {
        if (admin) {
            // 管理员查询
            return contestRepo.selectProblems(cid, false)
                    .collectList();
        }

        // 用户查询，同时返回每题结果
        return inviteeRepo.isInvited(cid, uid)
                .flatMapMany(isInvited -> {
                    if (Boolean.FALSE.equals(isInvited)) {
                        return Flux.error(new GenericException(HttpStatus.PAYMENT_REQUIRED, "请使用邀请码加入竞赛"));
                    }

                    return contestRepo.selectProblems(cid, true);
                })
                .flatMap(p -> Mono.just(p).zipWith(solutionRepo.selectResultOfContest(uid, cid, p.getProblemId())))
                .map(t -> t.getT1().setResultAndReturn(t.getT2()))
                .collectList();
    }

    public Mono<Problem> getProblem(@RequestHeader Integer uid, Integer cid, Integer pid) {
        return inviteeRepo.isInvited(cid, uid)
                .flatMap(isInvited -> {
                    if (Boolean.FALSE.equals(isInvited)) {
                        return Mono.error(new GenericException(HttpStatus.PAYMENT_REQUIRED, "未加入竞赛"));
                    }

                    return contestRepo.selectProblem(cid, pid);
                });
    }

    public Mono<HttpStatus> addProblemToContest(Integer cid, Integer pid) {
        return contestRepo.countProblems(cid)
                .flatMap(count -> count == 13 ?
                        Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "不能超过 13 题")) :
                        contestRepo.addProblem(cid, pid)
                                .map(rows -> rows > 0 ?
                                        HttpStatus.CREATED :
                                        HttpStatus.BAD_REQUEST
                                )
                );
    }

    public Mono<HttpStatus> removeProblem(Integer cid, Integer pid) {
        return contestRepo.removeProblem(cid, pid)
                .map(rows -> rows > 0 ?
                        HttpStatus.NO_CONTENT :
                        HttpStatus.GONE
                );
    }
}

package cloud.oj.core.controller;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.service.ContestService;
import cloud.oj.core.service.SystemSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("contest")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    private final SystemSettings systemSettings;

    /**
     * 获取竞赛的详细信息
     */
    @GetMapping(path = "detail")
    public Mono<ResponseEntity<Contest>> getContest(Integer contestId) {
        return contestService.getContest(contestId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * 获取竞赛
     */
    @GetMapping
    public Mono<ResponseEntity<PageData<Contest>>> getContests(@RequestParam(defaultValue = "1") Integer page,
                                                               @RequestParam(defaultValue = "15") Integer size) {
        return systemSettings.getSettings()
                .flatMap(settings -> settings.isShowAllContest() ?
                        contestService.getAllContest(page, size) :
                        contestService.getStartedContest(page, size)
                )
                .map(data -> data.getTotal() > 0 ?
                        ResponseEntity.ok(data) :
                        ResponseEntity.noContent().build()
                );
    }

    /**
     * 获取所有竞赛
     *
     * @return 竞赛列表
     */
    @GetMapping(path = "admin")
    public Mono<ResponseEntity<PageData<Contest>>> allContests(@RequestParam(defaultValue = "1") Integer page,
                                                               @RequestParam(defaultValue = "15") Integer size) {
        return contestService.getAllContestAdmin(page, size)
                .map(data -> data.getTotal() > 0 ?
                        ResponseEntity.ok(data) :
                        ResponseEntity.noContent().build()
                );
    }

    /**
     * 从已开始的竞赛中获取题目
     */
    @GetMapping(path = "problem")
    public Mono<ResponseEntity<List<Problem>>> getProblemsOfStarted(@RequestHeader Integer uid, Integer cid) {
        return contestService.getProblemsOfContest(uid, cid, false)
                .map(problems -> problems.isEmpty() ?
                        ResponseEntity.noContent().build() :
                        ResponseEntity.ok(problems)
                );
    }

    @GetMapping(path = "admin/problem")
    public Mono<ResponseEntity<List<Problem>>> getProblems(Integer contestId) {
        return contestService.getProblemsOfContest(null, contestId, true)
                .map(problems -> problems.isEmpty() ? ResponseEntity.noContent().build() :
                        ResponseEntity.ok(problems)
                );
    }

    /**
     * 从已开始的竞赛中获取题目的详细信息
     */
    @GetMapping(path = "problem/{cid}/{pid}")
    public Mono<ResponseEntity<Problem>> getProblemInContest(@RequestHeader Integer uid,
                                                             @PathVariable Integer cid,
                                                             @PathVariable Integer pid) {
        return contestService.getProblem(uid, cid, pid)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PutMapping(path = "admin/problem/order/{cid}")
    public Mono<ResponseEntity<?>> changeOrders(@PathVariable Integer cid, @RequestBody List<Integer> problems) {
        return contestService.changeOrders(cid, problems)
                .thenReturn(ResponseEntity.ok().build());
    }

    /**
     * 获取可用题目
     */
    @GetMapping("admin/problem_idle/{cid}")
    public Mono<ResponseEntity<PageData<Problem>>> getIdleProblems(@PathVariable Integer cid,
                                                                   @RequestParam(defaultValue = "1") Integer page,
                                                                   @RequestParam(defaultValue = "15") Integer limit) {
        return contestService.getIdleProblems(cid, page, limit)
                .map(data -> data.getTotal() > 0 ?
                        ResponseEntity.ok(data) :
                        ResponseEntity.noContent().build()
                );
    }

    /**
     * 创建竞赛
     */
    @PostMapping(path = "admin")
    public Mono<ResponseEntity<?>> create(@RequestBody Contest contest) {
        return contestService.create(contest)
                .map(status -> ResponseEntity.status(status).build());
    }

    /**
     * 更新竞赛
     */
    @PutMapping(path = "admin")
    public Mono<ResponseEntity<?>> update(@RequestBody Contest contest) {
        return contestService.updateContest(contest)
                .map(status -> ResponseEntity.status(status).build());
    }

    /**
     * 重新生成邀请码
     */
    @PutMapping(path = "admin/key/{cid}")
    public Mono<String> newInviteKey(@PathVariable Integer cid) {
        return contestService.newInviteKey(cid);
    }

    /**
     * 删除竞赛
     */
    @DeleteMapping(path = "admin/{cid}")
    public Mono<ResponseEntity<?>> delete(@PathVariable Integer cid) {
        return contestService.deleteContest(cid)
                .map(status -> ResponseEntity.status(status).build());
    }

    /**
     * 向竞赛添加题目
     */
    @PostMapping(path = "admin/problem/{cid}/{pid}")
    public Mono<ResponseEntity<?>> addProblem(@PathVariable Integer cid, @PathVariable Integer pid) {
        return contestService.addProblemToContest(cid, pid)
                .map(status -> ResponseEntity.status(status).build());
    }

    /**
     * 从竞赛移除题目
     */
    @DeleteMapping(path = "admin/problem/{cid}/{pid}")
    public Mono<ResponseEntity<?>> removeProblem(@PathVariable Integer cid, @PathVariable Integer pid) {
        return contestService.removeProblem(cid, pid)
                .map(status -> ResponseEntity.status(status).build());
    }

    /**
     * 加入竞赛
     */
    @PostMapping("/invitation/{cid}")
    public Mono<ResponseEntity<?>> inviteUser(@RequestHeader Integer uid, @PathVariable Integer cid, String key) {
        return contestService.inviteUserWithKey(cid, uid, key);
    }
}

package cloud.oj.core.controller;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.PagedList;
import cloud.oj.core.service.ContestService;
import cloud.oj.core.service.SystemSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("contest")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    private final SystemSettings systemSettings;

    /**
     * 获取竞赛
     *
     * @return 竞赛列表
     */
    @GetMapping
    public ResponseEntity<?> getContests(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "15") Integer limit) {
        PagedList contests;
        if (systemSettings.getSettings().isShowAllContest()) {
            contests = PagedList.resolve(contestService.getAllContest(page, limit));
        } else {
            contests = PagedList.resolve(contestService.getStartedContest(page, limit));
        }

        return contests.getCount() > 0 ?
                ResponseEntity.ok(contests)
                : ResponseEntity.noContent().build();
    }

    /**
     * 获取竞赛的详细信息
     *
     * @return {@link cloud.oj.core.entity.Problem}
     */
    @GetMapping(path = "detail")
    public ResponseEntity<?> getContest(Integer contestId) {
        return contestService.getContest(contestId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取所有竞赛
     *
     * @return 竞赛列表
     */
    @GetMapping(path = "admin")
    public ResponseEntity<?> allContests(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "15") Integer limit) {
        var contests = PagedList.resolve(contestService.getAllContestAdmin(page, limit));
        return contests.getCount() > 0 ?
                ResponseEntity.ok(contests)
                : ResponseEntity.noContent().build();
    }

    /**
     * 从已开始的竞赛中获取题目
     *
     * @return 题目列表
     */
    @GetMapping(path = "problem")
    public ResponseEntity<?> getProblemsFromStartedContest(@RequestHeader Integer uid, Integer contestId) {
        var problems = contestService.getProblemsFromContest(uid, contestId, false);
        return problems.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(problems);
    }

    /**
     * 从已开始的竞赛中获取题目的详细信息
     *
     * @return {@link cloud.oj.core.entity.Problem}
     */
    @GetMapping(path = "problem/{contestId}/{problemId}")
    public ResponseEntity<?> getProblemInContest(@RequestHeader Integer uid, @PathVariable Integer contestId, @PathVariable Integer problemId) {
        return contestService.getProblemInContest(uid, contestId, problemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 从竞赛中获取题目
     *
     * @return 返回题目列表
     */
    @GetMapping(path = "admin/problem")
    public ResponseEntity<?> getProblemsFromContest(Integer contestId) {
        var problems = contestService.getProblemsFromContest(null, contestId, true);
        return problems.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(problems);
    }

    @PutMapping(path = "admin/problem_order/{contestId}")
    public ResponseEntity<?> changeOrder(@PathVariable Integer contestId, @RequestBody List<Integer> problems) {
        contestService.changeOrder(contestId, problems);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取不在竞赛中的题目
     *
     * @return 题目列表
     */
    @GetMapping("admin/available_problem/{contestId}")
    public ResponseEntity<?> getProblemsNotInContest(@PathVariable Integer contestId,
                                                     @RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "15") Integer limit) {
        var problems = PagedList.resolve(contestService.getProblemsNotInContest(contestId, page, limit));
        return problems.getCount() > 0 ?
                ResponseEntity.ok(problems)
                : ResponseEntity.noContent().build();
    }

    /**
     * 创建竞赛
     */
    @PostMapping(path = "admin")
    public ResponseEntity<?> create(@RequestBody Contest contest) {
        return ResponseEntity.status(contestService.create(contest)).build();
    }

    /**
     * 更新竞赛
     */
    @PutMapping(path = "admin")
    public ResponseEntity<?> update(@RequestBody Contest contest) {
        return ResponseEntity.status(contestService.updateContest(contest)).build();
    }

    /**
     * 重新生成邀请码
     *
     * @return 新的邀请码
     */
    @PutMapping(path = "admin/gen_key/{contestId}")
    public ResponseEntity<String> newInviteKey(@PathVariable Integer contestId) {
        return ResponseEntity.ok(contestService.newInviteKey(contestId));
    }


    /**
     * 删除竞赛
     */
    @DeleteMapping(path = "admin/{contestId}")
    public ResponseEntity<?> delete(@PathVariable Integer contestId) {
        return ResponseEntity.status(contestService.deleteContest(contestId)).build();
    }

    /**
     * 向竞赛添加题目
     */
    @PostMapping(path = "admin/problem/{contestId}/{problemId}")
    public ResponseEntity<?> addProblem(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return ResponseEntity.status(contestService.addProblemToContest(contestId, problemId)).build();
    }

    /**
     * 从竞赛移除题目
     */
    @DeleteMapping(path = "admin/problem/{contestId}/{problemId}")
    public ResponseEntity<?> removeProblem(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return ResponseEntity.status(contestService.removeProblem(contestId, problemId)).build();
    }

    /**
     * 邀请用户到竞赛
     */
    @PostMapping("/invitation/{contestId}")
    public ResponseEntity<?> inviteUser(@RequestHeader Integer uid, @PathVariable Integer contestId, String key) {
        return contestService.inviteUserWithKey(contestId, uid, key);
    }
}

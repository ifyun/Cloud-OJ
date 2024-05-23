package cloud.oj.core.controller;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("contest")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    /**
     * 获取竞赛的详细信息
     */
    @GetMapping(path = "detail")
    public ResponseEntity<Contest> getContest(Integer cid) {
        return ResponseEntity.ok(contestService.getContest(cid));
    }

    /**
     * 获取所有竞赛
     */
    @GetMapping
    public ResponseEntity<PageData<Contest>> getContests(@RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "15") Integer size) {
        var data = contestService.getAllContest(page, size);
        return data.getTotal() > 0 ?
                ResponseEntity.ok(data) :
                ResponseEntity.noContent().build();
    }

    /**
     * 获取所有竞赛(Admin)
     */
    @GetMapping(path = "admin")
    public ResponseEntity<PageData<Contest>> getContestsAdmin(@RequestParam(defaultValue = "1") Integer page,
                                                              @RequestParam(defaultValue = "15") Integer size) {
        var data = contestService.getAllContestAdmin(page, size);
        return data.getTotal() > 0 ?
                ResponseEntity.ok(data) :
                ResponseEntity.noContent().build();
    }

    /**
     * 从已开始的竞赛中获取题目
     */
    @GetMapping(path = "problem")
    public ResponseEntity<List<Problem>> getProblemsOfStarted(@RequestHeader Integer uid, Integer cid) {
        var data = contestService.getProblemsOfContest(uid, cid, false);
        return data.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(data);
    }

    @GetMapping(path = "admin/problem")
    public ResponseEntity<List<Problem>> getProblems(Integer cid) {
        var data = contestService.getProblemsOfContest(null, cid, true);
        return data.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(data);
    }

    /**
     * 从已开始的竞赛中获取题目详情
     */
    @GetMapping(path = "problem/{cid}/{pid}")
    public ResponseEntity<Problem> getProblemInContest(@RequestHeader Integer uid,
                                                       @PathVariable Integer cid,
                                                       @PathVariable Integer pid) {
        return ResponseEntity.ok(contestService.getProblem(uid, cid, pid));
    }

    @PutMapping(path = "admin/problem/order/{cid}")
    public ResponseEntity<?> changeOrders(@PathVariable Integer cid, @RequestBody List<Integer> problems) {
        return ResponseEntity.status(contestService.changeOrders(cid, problems)).build();
    }

    /**
     * 获取可用题目
     */
    @GetMapping("admin/problem_idle/{cid}")
    public ResponseEntity<PageData<Problem>> getIdleProblems(@PathVariable Integer cid,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "15") Integer size) {
        var data = contestService.getIdleProblems(cid, page, size);
        return data.getTotal() > 0 ?
                ResponseEntity.ok(data) :
                ResponseEntity.noContent().build();
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
     */
    @PutMapping(path = "admin/key/{cid}")
    public ResponseEntity<String> newInviteKey(@PathVariable Integer cid) {
        return ResponseEntity.ok(contestService.newInviteKey(cid));
    }

    /**
     * 删除竞赛
     */
    @DeleteMapping(path = "admin/{cid}")
    public ResponseEntity<?> delete(@PathVariable Integer cid) {
        return ResponseEntity.status(contestService.deleteContest(cid)).build();
    }

    /**
     * 向竞赛添加题目
     */
    @PostMapping(path = "admin/problem/{cid}/{pid}")
    public ResponseEntity<?> addProblem(@PathVariable Integer cid, @PathVariable Integer pid) {
        return ResponseEntity.status(contestService.addProblemToContest(cid, pid)).build();
    }

    /**
     * 从竞赛移除题目
     */
    @DeleteMapping(path = "admin/problem/{cid}/{pid}")
    public ResponseEntity<?> removeProblem(@PathVariable Integer cid, @PathVariable Integer pid) {
        return ResponseEntity.status(contestService.removeProblem(cid, pid)).build();
    }

    /**
     * 加入竞赛
     */
    @PostMapping("/invitation/{cid}")
    public ResponseEntity<?> inviteUser(@RequestHeader Integer uid, @PathVariable Integer cid, String key) {
        return ResponseEntity.status(contestService.inviteUserWithKey(cid, uid, key)).build();
    }
}

package cloud.oj.core.controller;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.PagedList;
import cloud.oj.core.service.ContestService;
import cloud.oj.core.service.SystemSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("contest")
public class ContestController {

    @Resource
    private ContestService contestService;

    @Resource
    private SystemSettings systemSettings;

    /**
     * 获取竞赛/作业
     *
     * @return 竞赛列表
     */
    @GetMapping
    public ResponseEntity<?> contests(Integer page, Integer limit) {
        PagedList contests;
        if (systemSettings.getSettings().isShowNotStartedContest())
            contests = PagedList.resolve(contestService.getAllContest(page, limit));
        else
            contests = PagedList.resolve(contestService.getStartedContest(page, limit));
        return contests.getCount() > 0 ?
                ResponseEntity.ok(contests)
                : ResponseEntity.noContent().build();
    }

    /**
     * 获取竞赛/作业的详细信息
     *
     * @return {@link cloud.oj.core.entity.Problem}
     */
    @GetMapping(path = "detail")
    public ResponseEntity<?> contest(Integer contestId) {
        return contestService.getContest(contestId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取所有竞赛/作业
     *
     * @return 竞赛列表
     */
    @GetMapping(path = "admin")
    public ResponseEntity<?> allContests(Integer page, Integer limit) {
        var contests = PagedList.resolve(contestService.getAllContest(page, limit));
        return contests.getCount() > 0 ?
                ResponseEntity.ok(contests)
                : ResponseEntity.noContent().build();
    }

    /**
     * 从已开始的竞赛/作业中获取题目
     *
     * @return 题目列表
     */
    @GetMapping(path = "problem")
    public ResponseEntity<?> getProblemsFromStartedContest(Integer contestId, String userId) {
        var problems = contestService.getProblemsFromContest(userId, contestId, true);
        return problems.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(problems);
    }

    /**
     * 从已开始的竞赛/作业中获取题目的详细信息
     *
     * @return {@link cloud.oj.core.entity.Problem}
     */
    @GetMapping(path = "problem/{contestId}/{problemId}")
    public ResponseEntity<?> getProblemInContest(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return contestService.getProblemInContest(contestId, problemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 从竞赛/作业中获取题目
     *
     * @return 返回题目列表
     */
    @GetMapping(path = "admin/problem")
    public ResponseEntity<?> getProblemsFromContest(Integer contestId) {
        var problems = contestService.getProblemsFromContest(null, contestId, false);
        return problems.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(problems);
    }

    /**
     * 获取不在竞赛/作业中的题目
     *
     * @return 题目列表
     */
    @GetMapping("admin/problems_not_in_contest/{contestId}")
    public ResponseEntity<?> getProblemsNotInContest(@PathVariable Integer contestId, Integer page, Integer limit) {
        var problems = PagedList.resolve(contestService.getProblemsNotInContest(contestId, page, limit));
        return problems.getCount() > 0 ?
                ResponseEntity.ok(problems)
                : ResponseEntity.noContent().build();
    }

    /**
     * 创建竞赛/作业
     */
    @PostMapping(path = "admin")
    public ResponseEntity<?> addContest(@RequestBody Contest contest) {
        return ResponseEntity.status(contestService.addContest(contest)).build();
    }

    /**
     * 更新竞赛/作业
     */
    @PutMapping(path = "admin")
    public ResponseEntity<?> updateContest(@RequestBody Contest contest) {
        return ResponseEntity.status(contestService.updateContest(contest)).build();
    }

    /**
     * 删除竞赛/作业
     */
    @DeleteMapping(path = "admin/{contestId}")
    public ResponseEntity<?> deleteContest(@PathVariable Integer contestId) {
        return ResponseEntity.status(contestService.deleteContest(contestId)).build();
    }

    /**
     * 向竞赛/作业添加题目
     */
    @PostMapping(path = "admin/problem/{contestId}/{problemId}")
    public ResponseEntity<?> addProblem(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return ResponseEntity.status(contestService.addProblemToContest(contestId, problemId)).build();
    }

    /**
     * 从竞赛/作业中移除题目
     */
    @DeleteMapping(path = "admin/problem/{contestId}/{problemId}")
    public ResponseEntity<?> deleteProblem(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return ResponseEntity.status(contestService.removeProblem(contestId, problemId)).build();
    }
}

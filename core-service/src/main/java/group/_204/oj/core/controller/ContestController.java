package group._204.oj.core.controller;

import group._204.oj.core.service.ContestService;
import group._204.oj.core.service.SystemSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group._204.oj.core.model.Contest;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("contest")
public class ContestController implements CRUDController {

    @Resource
    private ContestService contestService;

    @Resource
    private SystemSettings systemSettings;

    /**
     * 获取竞赛/作业
     */
    @GetMapping()
    public ResponseEntity<?> contests(Integer page, Integer limit) {
        if (systemSettings.getSettings().isShowNotStartedContest())
            return buildGETResponse(contestService.getAllContest(page, limit));
        else
            return buildGETResponse(contestService.getStartedContest(page, limit));
    }

    /**
     * 获取竞赛/作业的详细信息
     */
    @GetMapping(path = "detail")
    public ResponseEntity<?> contest(Integer contestId) {
        return buildGETResponse(contestService.getContest(contestId));
    }

    /**
     * 获取所有竞赛/作业
     */
    @GetMapping(path = "admin")
    public ResponseEntity<?> allContests(Integer page, Integer limit) {
        return buildGETResponse(contestService.getAllContest(page, limit));
    }

    /**
     * 从已开始的竞赛/作业中获取题目
     */
    @GetMapping(path = "problem")
    public ResponseEntity<?> getProblemsFromStartedContest(Integer contestId, String userId) {
        var results = contestService.getProblemsFromContest(userId, contestId, true);
        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }

    /**
     * 从已开始的竞赛/作业中获取题目的详细信息
     */
    @GetMapping(path = "problem/{contestId}/{problemId}")
    public ResponseEntity<?> getProblemInContest(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return buildGETResponse(contestService.getProblemInContest(contestId, problemId));
    }

    /**
     * 从竞赛/作业中获取题目
     */
    @GetMapping(path = "admin/problem")
    public ResponseEntity<?> getProblemsFromContest(Integer contestId) {
        var results = contestService.getProblemsFromContest(null, contestId, false);
        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }

    /**
     * 获取不在竞赛/作业中的题目
     */
    @GetMapping("admin/problems_not_in_contest/{contestId}")
    public ResponseEntity<?> getProblemsNotInContest(@PathVariable Integer contestId, Integer page, Integer limit) {
        return buildGETResponse(contestService.getProblemsNotInContest(contestId, page, limit));
    }

    /**
     * 创建竞赛/作业
     */
    @PostMapping(path = "admin")
    public ResponseEntity<?> addContest(@RequestBody Contest contest) {
        return buildPOSTResponse(contestService.addContest(contest));
    }

    /**
     * 更新竞赛/作业
     */
    @PutMapping(path = "admin")
    public ResponseEntity<?> updateContest(@RequestBody Contest contest) {
        return buildPUTResponse(contestService.updateContest(contest));
    }

    /**
     * 删除竞赛/作业
     */
    @DeleteMapping(path = "admin/{contestId}")
    public ResponseEntity<?> deleteContest(@PathVariable Integer contestId) {
        return buildDELETEResponse(contestService.deleteContest(contestId));
    }

    /**
     * 向竞赛/作业添加题目
     */
    @PostMapping(path = "admin/problem/{contestId}/{problemId}")
    public ResponseEntity<?> addProblem(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return buildPOSTResponse(contestService.addProblemToContest(contestId, problemId));
    }

    /**
     * 从竞赛/作业中移除题目
     */
    @DeleteMapping(path = "admin/problem/{contestId}/{problemId}")
    public ResponseEntity<?> deleteProblem(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return buildDELETEResponse(contestService.removeProblem(contestId, problemId));
    }
}

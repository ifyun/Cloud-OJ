package group._204.oj.manager.controller;

import group._204.oj.manager.service.ContestService;
import group._204.oj.manager.service.SystemSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group._204.oj.manager.model.Contest;

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
    public ResponseEntity<?> Contests(Integer page, Integer limit) {
        if (systemSettings.getConfig().isShowNotStartedContest())
            return buildGETResponse(contestService.getAllContest(page, limit));
        else
            return buildGETResponse(contestService.getStartedContest(page, limit));
    }

    /**
     * 获取竞赛/作业的详细信息
     */
    @GetMapping(path = "detail")
    public ResponseEntity<?> Contest(Integer contestId) {
        return buildGETResponse(contestService.getContestByID(contestId));
    }

    /**
     * 获取所有竞赛/作业
     */
    @GetMapping(path = "pro")
    public ResponseEntity<?> allContests(Integer page, Integer limit) {
        return buildGETResponse(contestService.getAllContest(page, limit));
    }

    /**
     * 从已开始的竞赛/作业中获取题目
     */
    @GetMapping(path = "problem")
    public ResponseEntity<?> getProblemsFromStartedContest(Integer contestId, String userId,
                                                           Integer page, Integer limit) {
        return buildGETResponse(contestService.getProblemsFromContest(userId, contestId, true, page, limit));
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
    @GetMapping(path = "pro/problem")
    public ResponseEntity<?> getProblemsFromContest(Integer contestId, Integer page, Integer limit) {
        return buildGETResponse(contestService.getProblemsFromContest(null, contestId, false,
                page, limit));
    }

    /**
     * 获取不在竞赛/作业中的题目
     */
    @GetMapping("pro/problems_not_in_contest/{contestId}")
    public ResponseEntity<?> getProblemsNotInContest(@PathVariable Integer contestId, Integer page, Integer limit) {
        return buildGETResponse(contestService.getProblemsNotInContest(contestId, page, limit));
    }

    /**
     * 创建竞赛/作业
     */
    @PostMapping(path = "pro")
    public ResponseEntity<?> addContest(@RequestBody Contest contest) {
        return buildPOSTResponse(contestService.addContest(contest));
    }

    /**
     * 更新竞赛/作业
     */
    @PutMapping(path = "pro")
    public ResponseEntity<?> updateContest(@RequestBody Contest contest) {
        return buildPUTResponse(contestService.updateContest(contest));
    }

    /**
     * 删除竞赛/作业
     */
    @DeleteMapping(path = "pro/{contestId}")
    public ResponseEntity<?> deleteContest(@PathVariable Integer contestId) {
        return buildDELETEResponse(contestService.deleteContest(contestId));
    }

    /**
     * 向竞赛/作业添加题目
     */
    @PostMapping(path = "pro/problem/{contestId}/{problemId}")
    public ResponseEntity<?> addProblem(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return buildPOSTResponse(contestService.addProblemToContest(contestId, problemId));
    }

    /**
     * 从竞赛/作业中移除题目
     */
    @DeleteMapping(path = "pro/problem/{contestId}/{problemId}")
    public ResponseEntity<?> deleteProblem(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        return buildDELETEResponse(contestService.deleteProblemFromContest(contestId, problemId));
    }
}

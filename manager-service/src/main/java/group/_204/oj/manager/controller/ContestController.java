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

    @GetMapping("")
    public ResponseEntity<?> Contests(int page, int limit) {
        if (systemSettings.getConfig().isShowNotStartedContest())
            return buildGETResponse(contestService.getAllContest(page, limit));
        else
            return buildGETResponse(contestService.getStartedContest(page, limit));
    }

    @GetMapping("pro")
    public ResponseEntity<?> allContests(int page, int limit) {
        return buildGETResponse(contestService.getAllContest(page, limit));
    }

    @GetMapping("lang/{contestId}")
    public ResponseEntity<?> getLanguages(@PathVariable int contestId) {
        return buildGETResponse(contestService.getLanguages(contestId));
    }

    @GetMapping("{contestId}/problem")
    public ResponseEntity<?> getProblemsFromStartedContest(@PathVariable int contestId, String userId, int page, int limit) {
        return buildGETResponse(contestService.getProblemsFromContest(userId, contestId, true, page, limit));
    }

    @GetMapping("pro/{contestId}/problem")
    public ResponseEntity<?> getProblemsFromContest(@PathVariable int contestId, int page, int limit) {
        return buildGETResponse(contestService.getProblemsFromContest(null, contestId, false, page, limit));
    }

    @GetMapping("pro/{contestId}")
    public ResponseEntity<?> getProblemsNotInContest(@PathVariable int contestId, int page, int limit) {
        return buildGETResponse(contestService.getProblemsNotInContest(contestId, page, limit));
    }

    @PostMapping("pro")
    public ResponseEntity<?> addContest(@RequestBody Contest contest) {
        return buildPOSTResponse(contestService.addContest(contest));
    }

    @PutMapping("pro")
    public ResponseEntity<?> updateContest(@RequestBody Contest contest) {
        log.info(contest.toString());
        return buildPUTResponse(contestService.updateContest(contest));
    }

    @DeleteMapping("pro/{contestId}")
    public ResponseEntity<?> deleteContest(@PathVariable int contestId) {
        return buildDELETEResponse(contestService.deleteContest(contestId));
    }

    @PostMapping("pro/problem/{contestId}/{problemId}")
    public ResponseEntity<?> addProblem(@PathVariable int contestId, @PathVariable int problemId) {
        return buildPOSTResponse(contestService.addProblemToContest(contestId, problemId));
    }

    @DeleteMapping("pro/problem/{contestId}/{problemId}")
    public ResponseEntity<?> deleteProblem(@PathVariable int contestId, @PathVariable int problemId) {
        return buildDELETEResponse(contestService.deleteProblemFromContest(contestId, problemId));
    }
}

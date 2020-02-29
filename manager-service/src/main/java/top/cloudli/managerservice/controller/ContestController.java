package top.cloudli.managerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.model.Contest;
import top.cloudli.managerservice.service.ManagerService;

import javax.annotation.Resource;

@RestController
@RequestMapping("contest")
public class ContestController implements CRUDController {

    @Resource
    private ManagerService managerService;

    @GetMapping("")
    public ResponseEntity<?> startedContests(int page, int limit) {
        return buildGETResponse(managerService.getStartedContest((page - 1) * limit, limit));
    }

    @GetMapping("pro")
    public ResponseEntity<?> allContests(int page, int limit) {
        return buildGETResponse(managerService.getAllContest((page - 1) * limit, limit));
    }

    @GetMapping("/lang/{contestId}")
    public ResponseEntity<?> getLanguages(@PathVariable int contestId) {
        return buildGETResponse(managerService.getLanguages(contestId));
    }

    @GetMapping("{contestId}")
    public ResponseEntity<?> getProblems(@PathVariable int contestId, String userId, int page, int limit) {
        return buildGETResponse(managerService.getProblemsFromContest(userId, contestId, (page - 1) * limit, limit));
    }

    @GetMapping("pro/{contestId}")
    public ResponseEntity<?> getProblemsNotInContest(@PathVariable int contestId, int page, int limit) {
        return buildGETResponse(managerService.getProblemsNotInContest(contestId, (page - 1) * limit, limit));
    }

    @PostMapping("pro")
    public ResponseEntity<?> addContest(@RequestBody Contest contest) {
        return buildPOSTResponse(managerService.addContest(contest));
    }

    @PutMapping("pro")
    public ResponseEntity<?> updateContest(@RequestBody Contest contest) {
        return buildPUTResponse(managerService.updateContest(contest));
    }

    @DeleteMapping("pro/{contestId}")
    public ResponseEntity<?> deleteContest(@PathVariable int contestId) {
        return buildDELETEResponse(managerService.deleteContest(contestId));
    }

    @PostMapping("pro/problem/{contestId}/{problemId}")
    public ResponseEntity<?> addProblem(@PathVariable int contestId, @PathVariable int problemId) {
        return buildPOSTResponse(managerService.addProblemToContest(contestId, problemId));
    }

    @DeleteMapping("pro/problem/{contestId}/{problemId}")
    public ResponseEntity<?> deleteProblem(@PathVariable int contestId, @PathVariable int problemId) {
        return buildDELETEResponse(managerService.deleteProblemFromContest(contestId, problemId));
    }
}

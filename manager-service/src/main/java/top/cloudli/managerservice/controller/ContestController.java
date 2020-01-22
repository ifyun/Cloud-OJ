package top.cloudli.managerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.model.Contest;
import top.cloudli.managerservice.service.ManagerService;

import javax.annotation.Resource;

@RestController
@RequestMapping("contest")
public class ContestController {

    @Resource
    private ManagerService managerService;

    @GetMapping("")
    public ResponseEntity<?> contests(int page, int limit) {
        return managerService.getAllContest((page - 1) * limit, limit);
    }

    @GetMapping("{contestId}")
    public ResponseEntity<?> getProblems(@PathVariable int contestId, int page, int limit) {
        return managerService.getProblemsFromContest(contestId, (page - 1) * limit, limit);
    }

    @PostMapping("pro")
    public ResponseEntity<?> addContest(@RequestBody Contest contest) {
        return managerService.addContest(contest);
    }

    @DeleteMapping("pro/{contestId}")
    public ResponseEntity<?> deleteContest(@PathVariable int contestId) {
        return managerService.deleteContest(contestId);
    }

    @PostMapping("pro/problem/{contestId}/{problemId}")
    public ResponseEntity<?> addProblem(@PathVariable int contestId, @PathVariable int problemId) {
        return managerService.addProblemToContest(contestId, problemId);
    }

    @DeleteMapping("pro/problem/{contestId}/{problemId}")
    public ResponseEntity<?> deleteProblem(@PathVariable int contestId, @PathVariable int problemId) {
        return managerService.deleteProblemFromContest(contestId, problemId);
    }
}

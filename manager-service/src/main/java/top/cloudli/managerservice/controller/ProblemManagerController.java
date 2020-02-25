package top.cloudli.managerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.model.Problem;
import top.cloudli.managerservice.service.ManagerService;

import javax.annotation.Resource;

/**
 * 题目管理接口
 */
@RestController
@RequestMapping("problem")
public class ProblemManagerController {

    @Resource
    private ManagerService managerService;

    @GetMapping("")
    public ResponseEntity<?> getAllEnable(String userId, String keyword, int page, int limit) {
        if (keyword != null)
            return managerService.searchProblems(userId, keyword, (page - 1) * limit, limit, true);
        return managerService.getEnableProblems(userId, (page - 1) * limit, limit);
    }

    @GetMapping("pro")
    public ResponseEntity<?> getAll(String userId, String keyword, int page, int limit) {
        if (keyword != null)
            return managerService.searchProblems(userId, keyword, (page - 1) * limit, limit, false);
        return managerService.getProblems(userId, (page - 1) * limit, limit);
    }

    @GetMapping("{problemId}")
    public ResponseEntity<?> getEnable(@PathVariable Integer problemId, Integer contestId) {
        return contestId == null ? managerService.getEnableProblem(problemId) : managerService.getProblem(problemId);
    }

    @GetMapping("pro/{problemId}")
    public ResponseEntity<?> getSingle(@PathVariable Integer problemId) {
        return managerService.getProblem(problemId);
    }

    @PutMapping("pro")
    public ResponseEntity<Void> update(@RequestBody Problem problem) {
        return managerService.updateProblem(problem);
    }

    @PutMapping("pro/{problemId}")
    public ResponseEntity<Void> updateEnable(@PathVariable int problemId, boolean enable) {
        return managerService.updateProblemEnable(problemId, enable);
    }

    @PostMapping("pro")
    public ResponseEntity<?> add(@RequestBody Problem problem) {
        return managerService.addProblem(problem);
    }

    @DeleteMapping("pro/{problemId}")
    public ResponseEntity<Void> delete(@PathVariable int problemId) {
        return managerService.deleteProblem(problemId);
    }
}

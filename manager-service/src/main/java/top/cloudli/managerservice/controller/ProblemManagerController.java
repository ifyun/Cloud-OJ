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
    public ResponseEntity<?> getAllEnable(String keyword, int page, int limit) {
        if (keyword != null)
           return managerService.searchProblems(keyword, (page - 1) * limit, limit);
        return managerService.getEnableProblems((page - 1) * limit, limit);

    }

    @GetMapping("{problemId}")
    public ResponseEntity<?> getEnable(@PathVariable int problemId) {
        return managerService.getEnableProblem(problemId);
    }

    @GetMapping("pro/{problemId}")
    public ResponseEntity<?> getSingle(@PathVariable int problemId) {
        return managerService.getProblem(problemId);
    }

    @GetMapping("pro")
    public ResponseEntity<?> getAll(int page, int limit) {
        return managerService.getProblems((page - 1) * limit, limit);
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

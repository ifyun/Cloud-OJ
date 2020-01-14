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
    public ResponseEntity<?> getAllEnable(String keyword) {
        if (keyword != null)
           return managerService.searchProblems(keyword);
        return managerService.getEnableProblems();

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
    public ResponseEntity<?> getAll() {
        return managerService.getProblems();
    }

    @PutMapping("pro")
    public ResponseEntity<Void> update(@RequestBody Problem problem) {
        return managerService.updateProblem(problem);
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

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
    public ResponseEntity<?> getProblems() {
        return managerService.getProblems();
    }

    @GetMapping("{keyword}")
    public ResponseEntity<?> searchProblems(@PathVariable String keyword) {
        return managerService.searchProblems(keyword);
    }

    @PutMapping("")
    public ResponseEntity<Void> updateProblem(@RequestBody Problem problem) {
        return managerService.updateProblem(problem);
    }

    @PostMapping("")
    public ResponseEntity<?> addProblem(@RequestBody Problem problem) {
        return managerService.addProblem(problem);
    }

    @DeleteMapping("{problemId}")
    public ResponseEntity<Void> deleteProblem(@PathVariable int problemId) {
        return managerService.deleteProblem(problemId);
    }
}

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
    public ResponseEntity<?> getEnable() {
        return managerService.getEnableProblems();
    }

    @GetMapping("pro")
    public ResponseEntity<?> getAll() {
        return managerService.getProblems();
    }

    @GetMapping("{keyword}")
    public ResponseEntity<?> search(@PathVariable String keyword) {
        return managerService.searchProblems(keyword);
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

package top.cloudli.loadbalanced.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.loadbalanced.service.ManagerService;

import javax.annotation.Resource;

/**
 * 管理服务的负载均衡接口
 */
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Resource
    private ManagerService managerService;

    @GetMapping("problem")
    public Object getProblems() {
        return managerService.getProblems();
    }

    @GetMapping("problem/search/{keyword}")
    public ResponseEntity<Object> searchProblems(@PathVariable String keyword) {
        return managerService.searchProblems(keyword);
    }

    @PostMapping("problem")
    public ResponseEntity<String> addProblem(@RequestBody Object problem) {
        return managerService.addProblem(problem);
    }

    @PutMapping("problem")
    public ResponseEntity<Object> updateProblem(@RequestBody Object problem) {
        return managerService.updateProblem(problem);
    }

    @DeleteMapping("problem/{problemId}")
    public ResponseEntity<Object> deleteProblem(@PathVariable int problemId) {
        return managerService.deleteProblem(problemId);
    }
}

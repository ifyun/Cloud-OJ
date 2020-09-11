package group._204.oj.manager.controller;

import group._204.oj.manager.model.Problem;
import group._204.oj.manager.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 题目管理接口
 */
@RestController
@RequestMapping("problem")
public class ProblemController implements CRUDController {

    @Resource
    private ManagerService managerService;

    @GetMapping("")
    public ResponseEntity<?> getAllEnable(String userId, String keyword, int page, int limit) {
        return keyword != null ?
                buildGETResponse(managerService.searchProblems(userId, keyword, (page - 1) * limit, limit, true))
                : buildGETResponse(managerService.getEnableProblems(userId, (page - 1) * limit, limit));
    }

    @GetMapping("pro")
    public ResponseEntity<?> getAll(String userId, String keyword, int page, int limit) {
        return keyword != null ?
                buildGETResponse(managerService.searchProblems(userId, keyword, (page - 1) * limit, limit, false))
                : buildGETResponse(managerService.getProblems(userId, (page - 1) * limit, limit));
    }

    @GetMapping("{problemId}")
    public ResponseEntity<?> getEnable(@PathVariable Integer problemId, Integer contestId) {
        return contestId == null ?
                buildGETResponse(managerService.getEnableProblem(problemId))
                : buildGETResponse(managerService.getProblem(problemId));
    }

    @GetMapping("pro/{problemId}")
    public ResponseEntity<?> getSingle(@PathVariable Integer problemId) {
        return buildGETResponse(managerService.getProblem(problemId));
    }

    @PutMapping("pro")
    public ResponseEntity<Void> update(@RequestBody Problem problem) {
        return buildPUTResponse(managerService.updateProblem(problem));
    }

    @PutMapping("pro/{problemId}")
    public ResponseEntity<Void> updateEnable(@PathVariable int problemId, boolean enable) {
        return buildPUTResponse(managerService.updateProblemEnable(problemId, enable));
    }

    @PostMapping("pro")
    public ResponseEntity<?> add(@RequestBody Problem problem) {
        return buildPOSTResponse(managerService.addProblem(problem));
    }

    @DeleteMapping("pro/{problemId}")
    public ResponseEntity<Void> delete(@PathVariable int problemId) {
        return buildDELETEResponse(managerService.deleteProblem(problemId));
    }
}

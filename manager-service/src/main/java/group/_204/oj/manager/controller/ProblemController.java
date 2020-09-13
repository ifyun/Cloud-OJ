package group._204.oj.manager.controller;

import group._204.oj.manager.model.Problem;
import group._204.oj.manager.service.ProblemService;
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
    private ProblemService problemService;

    @GetMapping("")
    public ResponseEntity<?> getAllEnable(String userId, String keyword, int page, int limit) {
        return keyword != null ?
                buildGETResponse(problemService.searchProblems(userId, keyword, page, limit, true))
                : buildGETResponse(problemService.getEnableProblems(userId, page, limit));
    }

    @GetMapping("pro")
    public ResponseEntity<?> getAll(String userId, String keyword, int page, int limit) {
        return keyword != null ?
                buildGETResponse(problemService.searchProblems(userId, keyword, page, limit, false))
                : buildGETResponse(problemService.getProblems(userId, page, limit));
    }

    @GetMapping("{problemId}")
    public ResponseEntity<?> getEnable(@PathVariable Integer problemId, Integer contestId) {
        return contestId == null ?
                buildGETResponse(problemService.getEnableProblem(problemId))
                : buildGETResponse(problemService.getProblem(problemId));
    }

    @GetMapping("pro/{problemId}")
    public ResponseEntity<?> getSingle(@PathVariable Integer problemId) {
        return buildGETResponse(problemService.getProblem(problemId));
    }

    @PutMapping("pro")
    public ResponseEntity<Void> update(@RequestBody Problem problem) {
        return buildPUTResponse(problemService.updateProblem(problem));
    }

    @PutMapping("pro/{problemId}")
    public ResponseEntity<Void> updateEnable(@PathVariable int problemId, boolean enable) {
        return buildPUTResponse(problemService.toggleEnable(problemId, enable));
    }

    @PostMapping("pro")
    public ResponseEntity<?> add(@RequestBody Problem problem) {
        return buildPOSTResponse(problemService.addProblem(problem));
    }

    @DeleteMapping("pro/{problemId}")
    public ResponseEntity<Void> delete(@PathVariable int problemId) {
        return buildDELETEResponse(problemService.deleteProblem(problemId));
    }
}

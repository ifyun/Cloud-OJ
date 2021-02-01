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

    /**
     * 获取所有开放的题目
     *
     * @param userId  若不为空，可以获取题目的判题结果
     * @param keyword 关键字
     */
    @GetMapping()
    public ResponseEntity<?> getAllEnable(String userId, String keyword, Integer page, Integer limit) {
        return userId == null || userId.isEmpty() ?
                buildGETResponse(problemService.getAllEnabled(keyword, page, limit))
                : buildGETResponse(problemService.getAllWithState(keyword, userId, page, limit));
    }

    /**
     * 获取所有题目
     */
    @GetMapping(path = "pro")
    public ResponseEntity<?> getAll(String keyword, Integer page, Integer limit) {
        return buildGETResponse(problemService.getAll(keyword, page, limit));
    }

    /**
     * 获取单个题目（仅开放）
     */
    @GetMapping(path = "{problemId}")
    public ResponseEntity<?> getEnable(@PathVariable Integer problemId) {
        return buildGETResponse(problemService.getSingleEnabled(problemId));
    }

    /**
     * 获取单个题目，无论是否开放
     */
    @GetMapping(path = "pro/{problemId}")
    public ResponseEntity<?> getSingle(@PathVariable Integer problemId) {
        return buildGETResponse(problemService.getSingle(problemId));
    }

    /**
     * 更新题目
     */
    @PutMapping(path = "pro")
    public ResponseEntity<?> update(@RequestBody Problem problem) {
        return buildPUTResponse(problemService.update(problem));
    }

    /**
     * 切换题目的开放/关闭状态
     */
    @PutMapping(path = "pro/{problemId}")
    public ResponseEntity<?> toggleEnable(@PathVariable Integer problemId, Boolean enable) {
        return buildPUTResponse(problemService.toggleEnable(problemId, enable));
    }

    /**
     * 添加题目
     */
    @PostMapping(path = "pro", consumes = "application/json")
    public ResponseEntity<?> add(@RequestBody Problem problem) {
        return buildPOSTResponse(problemService.add(problem));
    }

    /**
     * 删除题目
     */
    @DeleteMapping(path = "pro/{problemId}")
    public ResponseEntity<?> delete(@PathVariable Integer problemId) {
        return buildDELETEResponse(problemService.delete(problemId));
    }
}

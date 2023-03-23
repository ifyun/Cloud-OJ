package cloud.oj.core.controller;

import cloud.oj.core.entity.PagedList;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 题目管理接口
 */
@RestController
@RequestMapping("problem")
public class ProblemController {

    private final ProblemService problemService;

    @Autowired
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    /**
     * 获取所有开放的题目
     *
     * @param keyword 关键字
     * @return 题目列表
     */
    @GetMapping
    public ResponseEntity<?> getAllEnable(String keyword, Integer page, Integer limit) {
        var problems = PagedList.resolve(problemService.getAllEnabled(keyword, page, limit));
        return problems.getCount() > 0 ?
                ResponseEntity.ok(problems)
                : ResponseEntity.noContent().build();
    }

    /**
     * 获取所有题目
     *
     * @return 题目列表
     */
    @GetMapping(path = "admin")
    public ResponseEntity<?> getAll(String keyword, Integer page, Integer limit) {
        var problems = PagedList.resolve(problemService.getAll(keyword, page, limit));
        return problems.getCount() > 0 ?
                ResponseEntity.ok(problems)
                : ResponseEntity.noContent().build();
    }

    /**
     * 获取单个题目（仅开放）
     *
     * @return {@link Problem}
     */
    @GetMapping(path = "{problemId}")
    public ResponseEntity<?> getEnable(@PathVariable Integer problemId) {
        return problemService.getSingleEnabled(problemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取单个题目，无论是否开放
     *
     * @return {@link Problem}
     */
    @GetMapping(path = "admin/{problemId}")
    public ResponseEntity<?> getSingle(@PathVariable Integer problemId) {
        return problemService.getSingle(problemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 更新题目
     */
    @PutMapping(path = "admin")
    public ResponseEntity<?> update(@RequestBody Problem problem) {
        return ResponseEntity.status(problemService.update(problem)).build();
    }

    /**
     * 切换题目的开放/关闭状态
     */
    @PutMapping(path = "admin/{problemId}")
    public ResponseEntity<?> toggleEnable(@PathVariable Integer problemId, Boolean enable) {
        return ResponseEntity.status(problemService.toggleEnable(problemId, enable)).build();
    }

    /**
     * 添加题目
     */
    @PostMapping(path = "admin", consumes = "application/json")
    public ResponseEntity<?> add(@RequestBody Problem problem) {
        return ResponseEntity.status(problemService.add(problem)).build();
    }

    /**
     * 删除题目
     */
    @DeleteMapping(path = "admin/{problemId}")
    public ResponseEntity<?> delete(@PathVariable Integer problemId) {
        return ResponseEntity.status(problemService.delete(problemId)).build();
    }
}
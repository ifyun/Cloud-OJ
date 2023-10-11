package cloud.oj.core.controller;

import cloud.oj.core.entity.PagedList;
import cloud.oj.core.entity.Solution;
import cloud.oj.core.service.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 提交记录/判题结果接口
 */
@RestController
@RequestMapping("solution")
public class SolutionController {

    private final SolutionService solutionService;

    @Autowired
    public SolutionController(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    /**
     * 根据过滤条件获取提交记录
     *
     * @param filter      1: by problemId, 2: by title
     * @param filterValue problemId/title
     */
    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader Integer uid,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "15") int limit,
                                    Integer filter,
                                    String filterValue) {
        var solutions = PagedList.resolve(solutionService.getSolutions(uid, page, limit, filter, filterValue));
        return solutions.getCount() > 0 ?
                ResponseEntity.ok(solutions)
                : ResponseEntity.noContent().build();
    }

    /**
     * 获取判题结果
     *
     * @return {@link SseEmitter} 由服务端轮询并推送结果
     */
    @GetMapping("{uid}/{time}")
    public SseEmitter getByUidAndTime(@PathVariable Integer uid, @PathVariable Long time) {
        return solutionService.getSolutionByUidAndTime(uid, time);
    }

    @GetMapping("{solutionId}")
    public ResponseEntity<Solution> getBySolution(@RequestHeader Integer uid, @PathVariable Integer solutionId) {
        var data = solutionService.getSolution(uid, solutionId);
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}

package cloud.oj.core.controller;

import cloud.oj.core.entity.PagedList;
import cloud.oj.core.service.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 提交记录/判题结果接口
 */
@RestController
@RequestMapping("history")
public class SolutionController {

    private final SolutionService solutionService;

    @Autowired
    public SolutionController(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    /**
     * 获取提交记录
     */
    @GetMapping
    public ResponseEntity<?> getJudged(@RequestHeader String userId, Integer page, Integer limit,
                                       Integer problemId, String title) {
        var solutions = PagedList.resolve(solutionService.getHistories(userId, page, limit, problemId, title));
        return solutions.getCount() > 0 ?
                ResponseEntity.ok(solutions)
                : ResponseEntity.noContent().build();
    }

    /**
     * 获取判题结果
     *
     * @return {@link cloud.oj.core.entity.JudgeResult}
     */
    @GetMapping("{solutionId}")
    public ResponseEntity<?> getBySolutionId(@PathVariable String solutionId) {
        return solutionService.getBySolutionId(solutionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

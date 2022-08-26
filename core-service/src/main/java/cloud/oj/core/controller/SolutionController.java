package cloud.oj.core.controller;

import cloud.oj.core.model.JudgeResult;
import cloud.oj.core.service.SolutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 提交记录/判题结果接口
 */
@RestController
@RequestMapping("history")
public class SolutionController implements CRUDController {

    @Resource
    private SolutionService solutionService;

    /**
     * 获取提交记录
     */
    @GetMapping()
    public ResponseEntity<?> getJudged(@RequestHeader String userId, Integer page, Integer limit,
                                       Integer problemId, String title) {
        return buildGETResponse(solutionService.getHistories(userId, page, limit, problemId, title));
    }

    /**
     * 获取判题结果
     */
    @GetMapping("{solutionId}")
    public ResponseEntity<?> getBySolutionId(@PathVariable String solutionId) {
        JudgeResult result = solutionService.getBySolutionId(solutionId);
        return result != null ?
                ResponseEntity.ok(result) :
                ResponseEntity.noContent().build();
    }
}

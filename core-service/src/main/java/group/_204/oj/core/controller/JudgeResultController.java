package group._204.oj.core.controller;

import group._204.oj.core.model.JudgeResult;
import group._204.oj.core.service.JudgeResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("history")
public class JudgeResultController implements CRUDController {

    @Resource
    private JudgeResultService judgeResultService;

    /**
     * 获取提交记录
     */
    @GetMapping()
    public ResponseEntity<?> getJudged(@RequestHeader String userId, Integer page, Integer limit,
                                       Integer problemId, String title) {
        return buildGETResponse(judgeResultService.getHistories(userId, page, limit, problemId, title));
    }

    /**
     * 获取判题结果
     */
    @GetMapping("{solutionId}")
    public ResponseEntity<?> getBySolutionId(@PathVariable String solutionId) {
        JudgeResult result = judgeResultService.getBySolutionId(solutionId);
        return result != null ?
                ResponseEntity.ok(result) :
                ResponseEntity.noContent().build();
    }
}

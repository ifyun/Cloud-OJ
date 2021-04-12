package group._204.oj.core.controller;

import group._204.oj.core.service.JudgeResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

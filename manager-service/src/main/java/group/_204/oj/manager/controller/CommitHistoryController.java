package group._204.oj.manager.controller;

import group._204.oj.manager.service.CommitHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("result")
public class CommitHistoryController implements CRUDController {

    @Resource
    private CommitHistoryService commitHistoryService;

    @GetMapping("")
    public ResponseEntity<?> getJudged(String userId, int page, int limit) {
        return buildGETResponse(commitHistoryService.getJudged(userId, page, limit));
    }
}

package group._204.oj.manager.controller;

import group._204.oj.manager.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("result")
public class JudgeResultController implements CRUDController {

    @Resource
    private ManagerService managerService;

    @GetMapping("")
    public ResponseEntity<?> getJudged(String userId, int page, int limit) {
        return buildGETResponse(managerService.getJudged(userId, (page - 1) * limit, limit));
    }
}

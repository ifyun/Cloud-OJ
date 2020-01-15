package top.cloudli.managerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cloudli.managerservice.service.ManagerService;

import javax.annotation.Resource;

@RestController
@RequestMapping("result")
public class JudgeResultController {

    @Resource
    private ManagerService managerService;

    @GetMapping("")
    public ResponseEntity<?> getJudged(String userId, int page, int limit) {
        return managerService.getJudged(userId, (page - 1) * limit, limit);
    }
}

package top.cloudli.managerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cloudli.managerservice.service.ManagerService;

import javax.annotation.Resource;

@RestController
@RequestMapping("ranking")
public class RankingController {
    @Resource
    private ManagerService managerService;

    @GetMapping("")
    public ResponseEntity<?> getRankingList(int page, int limit) {
        return managerService.getRanking((page - 1) * limit, limit);
    }
}

package group._204.oj.manager.controller;

import group._204.oj.manager.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("ranking")
public class RankingController implements CRUDController {
    @Resource
    private ManagerService managerService;

    @GetMapping("")
    public ResponseEntity<?> getRankingList(int page, int limit) {
        return buildGETResponse(managerService.getRanking((page - 1) * limit, limit));
    }

    @GetMapping("contest/{contestId}")
    public ResponseEntity<?> getContestRanking(@PathVariable int contestId, int page, int limit) {
        return buildGETResponse(managerService.getContestRanking(contestId, (page - 1) * limit, limit));
    }
}

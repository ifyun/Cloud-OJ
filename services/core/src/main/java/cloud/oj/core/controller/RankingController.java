package cloud.oj.core.controller;

import cloud.oj.core.entity.PagedList;
import cloud.oj.core.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    /**
     * 获取排行榜
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getRankingList(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "15") Integer limit) {
        var rankings = PagedList.resolve(rankingService.getRanking(page, limit));
        return rankings.getCount() > 0 ?
                ResponseEntity.ok(rankings)
                : ResponseEntity.noContent().build();
    }

    /**
     * 获取竞赛排行榜
     */
    @GetMapping(path = "contest/{contestId}")
    public ResponseEntity<?> getContestRanking(@PathVariable Integer contestId) {
        var scoreboard = rankingService.getContestRanking(contestId);
        return scoreboard.getRanking().isEmpty() ? ResponseEntity.noContent().build() :
                ResponseEntity.ok(scoreboard);
    }

    /**
     * 获取竞赛排行榜(管理员用)
     */
    @GetMapping(path = "admin/contest/{contestId}")
    public ResponseEntity<?> getRankingListAdmin(@PathVariable Integer contestId) {
        return ResponseEntity.ok(rankingService.getContestRanking(contestId));
    }
}

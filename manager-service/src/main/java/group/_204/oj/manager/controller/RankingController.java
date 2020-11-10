package group._204.oj.manager.controller;

import group._204.oj.manager.dao.ContestDao;
import group._204.oj.manager.model.Msg;
import group._204.oj.manager.service.RankingService;
import group._204.oj.manager.service.SystemSettings;
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
    private RankingService rankingService;

    @Resource
    private ContestDao contestDao;

    @Resource
    private SystemSettings systemSettings;

    @GetMapping("")
    public ResponseEntity<?> getRankingList(int page, int limit) {
        return buildGETResponse(rankingService.getRanking(page, limit));
    }

    @GetMapping("contest/{contestId}")
    public ResponseEntity<?> getContestRanking(@PathVariable int contestId, int page, int limit) {
        if (systemSettings.getConfig().isShowRankingAfterEnded() && !contestDao.isContestEnded(contestId)) {
            return ResponseEntity.status(403).body(new Msg("结束后才可查看"));
        }

        return buildGETResponse(rankingService.getContestRanking(contestId, page, limit));
    }

    @GetMapping("pro/contest/{contestId}")
    public ResponseEntity<?> getRankingListAdmin(@PathVariable int contestId, int page, int limit) {
        return buildGETResponse(rankingService.getContestRanking(contestId, page, limit));
    }

    @GetMapping("pro/contest/detail")
    public ResponseEntity<?> getDetail(int contestId, String userId) {
        return buildGETResponse(rankingService.getDetail(contestId, userId));
    }
}

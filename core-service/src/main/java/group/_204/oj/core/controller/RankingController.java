package group._204.oj.core.controller;

import group._204.oj.core.dao.ContestDao;
import group._204.oj.core.model.Msg;
import group._204.oj.core.service.RankingService;
import group._204.oj.core.service.SystemSettings;
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

    /**
     * 获取排行榜
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getRankingList(Integer page, Integer limit) {
        return buildGETResponse(rankingService.getRanking(page, limit));
    }

    /**
     * 获取竞赛排行榜
     */
    @GetMapping(path = "contest/{contestId}")
    public ResponseEntity<?> getContestRanking(@PathVariable Integer contestId, Integer page, Integer limit) {
        if (systemSettings.getConfig().isShowRankingAfterEnded() && !contestDao.isContestEnded(contestId)) {
            return ResponseEntity.status(403).body(new Msg("结束后才可查看"));
        }

        return buildGETResponse(rankingService.getContestRanking(contestId, page, limit));
    }

    /**
     * 获取竞赛排行榜(管理员用)
     */
    @GetMapping(path = "pro/contest/{contestId}")
    public ResponseEntity<?> getRankingListAdmin(@PathVariable Integer contestId, Integer page, Integer limit) {
        return buildGETResponse(rankingService.getContestRanking(contestId, page, limit));
    }

    /**
     * 获取用户的详细得分情况
     */
    @GetMapping(path = "pro/contest/detail")
    public ResponseEntity<?> getDetail(Integer contestId, String userId) {
        return buildGETResponse(rankingService.getDetail(contestId, userId));
    }
}

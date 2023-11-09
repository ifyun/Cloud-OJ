package cloud.oj.core.service;

import cloud.oj.core.dao.ContestDao;
import cloud.oj.core.dao.RankingDao;
import cloud.oj.core.entity.ProblemOrder;
import cloud.oj.core.entity.RankingContest;
import cloud.oj.core.entity.ScoreDetail;
import cloud.oj.core.error.GenericException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.TreeSet;

/**
 * 排名相关业务
 */
@Service
public class RankingService {

    private final RankingDao rankingDao;

    private final ContestDao contestDao;

    private final SystemSettings systemSettings;

    public RankingService(RankingDao rankingDao, ContestDao contestDao, SystemSettings systemSettings) {
        this.rankingDao = rankingDao;
        this.contestDao = contestDao;
        this.systemSettings = systemSettings;
    }

    public List<List<?>> getRanking(int page, int limit) {
        return rankingDao.get((page - 1) * limit, limit);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RankingContest getContestRanking(Integer cid) {
        if (systemSettings.getSettings().isAlwaysShowRanking() && !contestDao.getState(cid).isEnded()) {
            throw new GenericException(HttpStatus.FORBIDDEN, "结束后才可查看");
        }

        var contest = contestDao.getContestById(cid);

        if (contest == null) {
            throw new GenericException(HttpStatus.NOT_FOUND, "竞赛不存在");
        }

        var data = new RankingContest(contest.withoutKey());    // 排名顶层包装
        var problemOrders = contestDao.getProblemOrders(cid);   // 题目 id 和 order
        var ranking = rankingDao.getForContest(cid);            // 排名

        ranking.forEach((r) -> {
            var detailList = rankingDao.getDetail(r.getUid(), cid);
            // 取 order 字段到 detailList
            detailList.forEach(d -> {
                var order = problemOrders.stream().filter(p -> d.getProblemId().equals(p.getProblemId()))
                        .findAny()
                        // 理论上不会为 null
                        .orElse(new ProblemOrder(0))
                        .getOrder();
                d.setOrder(order);
            });
            // 使用 Set 合并没有提交过的题目，按 order 字段排序
            TreeSet<ScoreDetail> detailSet = new TreeSet<>(detailList);
            problemOrders.forEach((p) -> detailSet.add(new ScoreDetail(p.getProblemId(), p.getOrder())));
            r.setDetails(detailSet);
        });
        // 取 problemId，按 order 字段排序
        var idList = problemOrders.stream().sorted().map(ProblemOrder::getProblemId).toList();
        data.setProblemIds(idList);
        data.setRanking(ranking);

        return data;
    }
}

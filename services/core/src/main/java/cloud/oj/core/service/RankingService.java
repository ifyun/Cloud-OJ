package cloud.oj.core.service;

import cloud.oj.core.dao.ContestDao;
import cloud.oj.core.dao.RankingDao;
import cloud.oj.core.entity.ScoreDetail;
import cloud.oj.core.entity.RankingContest;
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
        return rankingDao.getRanking((page - 1) * limit, limit);
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

        var data = new RankingContest(contest.withoutKey());
        var idList = contestDao.getProblemIds(cid);
        var ranking = rankingDao.getContestRanking(cid);

        ranking.forEach((e) -> {
            var t = rankingDao.getDetail(e.getUid(), cid);
            // 使用 Set 合并没有提交过的题目(数据为 null)
            TreeSet<ScoreDetail> details = new TreeSet<>(t);
            idList.forEach((id) -> details.add(new ScoreDetail(id)));
            e.setDetails(details);
        });

        data.setProblemIds(idList);
        data.setRanking(ranking);

        return data;
    }
}

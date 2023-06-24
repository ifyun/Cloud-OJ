package cloud.oj.core.service;

import cloud.oj.core.dao.ContestDao;
import cloud.oj.core.dao.RankingDao;
import cloud.oj.core.entity.Solution;
import cloud.oj.core.error.GenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 排名相关业务
 */
@Service
public class RankingService {

    private final RankingDao rankingDao;

    private final ContestDao contestDao;

    private final SystemSettings systemSettings;

    @Autowired
    public RankingService(RankingDao rankingDao, ContestDao contestDao, SystemSettings systemSettings) {
        this.rankingDao = rankingDao;
        this.contestDao = contestDao;
        this.systemSettings = systemSettings;
    }

    public List<List<?>> getRanking(int page, int limit) {
        return rankingDao.getRanking((page - 1) * limit, limit);
    }

    public List<List<?>> getContestRanking(int contestId, int page, int limit) {
        if (systemSettings.getSettings().isAlwaysShowRanking() && !contestDao.getState(contestId).isEnded()) {
            throw new GenericException(HttpStatus.FORBIDDEN.value(), "结束后才可查看");
        }

        return rankingDao.getContestRanking(contestId, (page - 1) * limit, limit);
    }

    public List<Solution> getDetail(int contestId, String userId) {
        return rankingDao.getDetailById(contestId, userId);
    }
}

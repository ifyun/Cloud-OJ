package cloud.oj.core.service;

import cloud.oj.core.dao.ContestDao;
import cloud.oj.core.dao.RankingDao;
import cloud.oj.core.error.GenericException;
import cloud.oj.core.entity.JudgeResult;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 排名相关业务
 */
@Service
public class RankingService {

    @Resource
    private RankingDao rankingDao;

    @Resource
    private ContestDao contestDao;

    @Resource
    private SystemSettings systemSettings;

    public List<List<?>> getRanking(int page, int limit) {
        return rankingDao.getRanking((page - 1) * limit, limit);
    }

    public List<List<?>> getContestRanking(int contestId, int page, int limit) {
        if (systemSettings.getSettings().isShowRankingAfterEnded() && !contestDao.isContestEnded(contestId)) {
            throw new GenericException(HttpStatus.FORBIDDEN.value(), "结束后才可查看");
        }

        return rankingDao.getContestRanking(contestId, (page - 1) * limit, limit);
    }

    public List<JudgeResult> getDetail(int contestId, String userId) {
        return rankingDao.getDetail(contestId, userId);
    }
}

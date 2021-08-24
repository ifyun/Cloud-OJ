package group._204.oj.core.service;

import group._204.oj.core.dao.SolutionDao;
import group._204.oj.core.model.JudgeResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SolutionService {

    @Resource
    private SolutionDao solutionDao;

    public List<List<?>> getHistories(String userId, int page, int limit, Integer problemId, String title) {
        if (problemId != null) {
            return solutionDao.getHistoryByProblemId(userId, (page - 1) * limit, limit, problemId);
        } else if (title != null && !title.isEmpty()) {
            return solutionDao.getHistoryByTitle(userId, (page - 1) * limit, limit, title);
        } else {
            return solutionDao.getHistory(userId, (page - 1) * limit, limit);
        }
    }

    public JudgeResult getBySolutionId(String solutionId) {
        return solutionDao.getResultBySolutionId(solutionId);
    }
}

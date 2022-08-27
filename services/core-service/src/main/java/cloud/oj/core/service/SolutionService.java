package cloud.oj.core.service;

import cloud.oj.core.entity.JudgeResult;
import cloud.oj.core.dao.SolutionDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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

    public Optional<JudgeResult> getBySolutionId(String solutionId) {
        return Optional.ofNullable(solutionDao.getResultBySolutionId(solutionId));
    }
}

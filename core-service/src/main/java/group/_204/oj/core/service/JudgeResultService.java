package group._204.oj.core.service;

import group._204.oj.core.dao.JudgeResultDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class JudgeResultService {

    @Resource
    private JudgeResultDao judgeResultDao;

    public List<List<?>> getHistories(String userId, int page, int limit, Integer problemId, String title) {
        if (problemId != null) {
            return judgeResultDao.getHistoryByProblemId(userId, (page - 1) * limit, limit, problemId);
        } else if (title != null && !title.isEmpty()) {
            return judgeResultDao.getHistoryByTitle(userId, (page - 1) * limit, limit, title);
        } else {
            return judgeResultDao.getHistory(userId, (page - 1) * limit, limit);
        }
    }
}

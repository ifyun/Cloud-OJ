package group._204.oj.manager.service;

import group._204.oj.manager.dao.JudgeResultDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommitHistoryService {

    @Resource
    private JudgeResultDao judgeResultDao;

    public List<List<?>> getJudged(String userId, int page, int limit) {
        return judgeResultDao.getHistoryByUserId(userId, (page - 1) * limit, limit);
    }
}

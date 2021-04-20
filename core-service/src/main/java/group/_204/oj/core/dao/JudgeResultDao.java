package group._204.oj.core.dao;

import group._204.oj.core.model.JudgeResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JudgeResultDao {
    JudgeResult getResultBySolutionId(String solutionId);

    List<List<?>> getHistory(String userId, int start, int limit);

    List<List<?>> getHistoryByProblemId(String userId, int start, int limit, Integer problemId);

    List<List<?>> getHistoryByTitle(String userId, int start, int limit, String title);
}

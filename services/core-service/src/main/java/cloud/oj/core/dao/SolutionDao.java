package cloud.oj.core.dao;

import cloud.oj.core.entity.JudgeResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SolutionDao {
    JudgeResult getResultBySolutionId(String solutionId);

    List<List<?>> getHistory(String userId, int start, int limit);

    List<List<?>> getHistoryByProblemId(String userId, int start, int limit, Integer problemId);

    List<List<?>> getHistoryByTitle(String userId, int start, int limit, String title);
}

package cloud.oj.core.dao;

import cloud.oj.core.entity.JudgeResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SolutionDao {
    JudgeResult getSolutionById(String solutionId);

    List<List<?>> getSolutionsByUser(String userId, int start, int limit);

    List<List<?>> getSolutionsByProblemId(String userId, int start, int limit, Integer problemId);

    List<List<?>> getSolutionsByTitle(String userId, int start, int limit, String title);
}

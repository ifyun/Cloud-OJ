package cloud.oj.core.dao;

import cloud.oj.core.entity.JudgeResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SolutionDao {
    JudgeResult getSolutionById(String solutionId, boolean showPassed);

    List<List<?>> getSolutionsByUser(String userId, Integer start, Integer limit, Integer filter, String filterValue);
}

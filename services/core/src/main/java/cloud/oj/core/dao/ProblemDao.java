package cloud.oj.core.dao;

import cloud.oj.core.entity.Problem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProblemDao {

    List<List<?>> getAll(int start, int limit, String keyword);

    List<List<?>> getAllEnabled(int start, int limit, String keyword);

    Problem getById(Integer problemId, boolean enable);

    Problem getByIdFromContest(Integer contestId, Integer problemId);

    int add(Problem problem);

    int update(Problem problem);

    int delete(Integer problemId);

    Integer isInContest(Integer problemId);

    Boolean isEnable(Integer problemId);

    int setEnable(Integer problemId, boolean enable);
}

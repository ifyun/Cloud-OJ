package group._204.oj.core.dao;

import group._204.oj.core.model.Problem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProblemDao {

    List<List<?>> getAll(int start, int limit, String keyword);

    List<List<?>> getAllEnabled(int start, int limit, String keyword);

    List<List<?>> getWithState(int start, int limit, String userId, String keyword);

    List<Problem> backup();

    Problem getSingle(Integer problemId, boolean enable);

    Problem getSingleInContest(Integer contestId, Integer problemId);

    int add(Problem problem);

    int update(Problem problem);

    int delete(Integer problemId);

    Integer isInContest(Integer problemId);

    int toggleEnable(Integer problemId, boolean enable);
}

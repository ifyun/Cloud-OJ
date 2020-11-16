package group._204.oj.manager.dao;

import group._204.oj.manager.model.Problem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProblemDao {

    List<List<?>> getAll(String userId, int start, int limit, boolean enable);

    List<Problem> backup();

    List<List<?>> search(String userId, String keyword, int start, int limit, boolean enable);

    Problem getSingle(Integer problemId, boolean enable);

    Problem getProblemInContest(Integer contestId, Integer problemId);

    int add(Problem problem);

    int update(Problem problem);

    int delete(Integer problemId);

    Integer inContest(Integer problemId);

    int toggleEnable(Integer problemId, boolean enable);
}

package group._204.oj.manager.dao;

import group._204.oj.manager.model.Problem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProblemDao {

    List<List<?>> getAll(String userId, int start, int limit, boolean enable);

    List<Problem> backup();

    List<List<?>> search(String userId, String keyword, int start, int limit, boolean enable);

    Problem getSingle(int problemId, boolean enable);

    int add(Problem problem);

    int update(Problem problem);

    int delete(int problemId);

    int inContest(int problemId);

    int toggleEnable(int problemId, boolean enable);
}

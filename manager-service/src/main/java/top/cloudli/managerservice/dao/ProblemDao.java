package top.cloudli.managerservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.managerservice.model.Problem;

import java.util.List;

@Mapper
public interface ProblemDao {

    List<List<?>> getAll(String userId, int start, int limit, boolean enable);

    List<List<?>> search(String userId, String keyword, int start, int limit, boolean enable);

    Problem getSingle(int problemId, boolean enable);

    int add(Problem problem);

    int update(Problem problem);

    int delete(int problemId);

    int updateEnable(int problemId, boolean enable);
}

package group._204.oj.judge.dao;

import group._204.oj.judge.model.Limit;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProblemDao {
    Limit getLimit(int problemId);
}

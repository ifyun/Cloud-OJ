package cloud.oj.judge.dao;

import cloud.oj.judge.model.Limit;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProblemDao {
    Limit getLimit(int problemId);
    String getSql(int problemId);
}

package cloud.oj.judge.dao;

import cloud.oj.judge.entity.Problem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProblemDao {
    Problem getById(int problemId);

    boolean isEnable(int problemId);
}

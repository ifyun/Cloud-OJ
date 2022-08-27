package cloud.oj.judge.dao;

import cloud.oj.judge.model.Solution;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SolutionDao {
    Double getMaxScoreOfUser(String userId, Integer problemId, Integer contestId);

    int add(Solution solution);

    int update(Solution solution);
}

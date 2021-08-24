package group._204.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;
import group._204.oj.judge.model.Solution;

@Mapper
public interface SolutionDao {
    Double getMaxScoreOfUser(String userId, Integer problemId, Integer contestId);

    int add(Solution solution);

    int update(Solution solution);
}

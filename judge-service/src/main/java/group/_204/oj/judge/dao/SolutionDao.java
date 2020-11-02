package group._204.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;
import group._204.oj.judge.model.JudgeResult;
import group._204.oj.judge.model.Solution;

@Mapper
public interface SolutionDao {
    JudgeResult getResultBySolutionId(String solutionId);

    int add(Solution solution);

    int update(Solution solution);
}

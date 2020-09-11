package group._204.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;
import group._204.oj.judge.model.JudgeResult;
import group._204.oj.judge.model.Solution;

import java.util.List;

@Mapper
public interface SolutionDao {
    List<Solution> getSubmitted();  // 获取已提交的答案

    JudgeResult getResultBySolutionId(String solutionId);        // 获取单条 solution

    int add(Solution solution);

    int update(Solution solution);
}

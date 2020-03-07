package top.cloudli.judgeservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.judgeservice.model.JudgeResult;
import top.cloudli.judgeservice.model.Solution;

import java.util.List;

@Mapper
public interface SolutionDao {
    List<Solution> getSubmitted();  // 获取已提交的答案

    JudgeResult getResultBySolutionId(int solutionId);        // 获取单条 solution

    int add(Solution solution);

    int update(Solution solution);
}

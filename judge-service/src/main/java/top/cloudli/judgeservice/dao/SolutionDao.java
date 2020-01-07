package top.cloudli.judgeservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.judgeservice.model.Solution;

import java.util.List;

@Mapper
public interface SolutionDao {
    List<Solution> getSubmitted();  // 获取已提交的答案

    List<Solution> getJudged();     // 获取判题结果

    List<Solution> getJudgedByUserId(String userId);       // 获取指定用户的判题结果

    Solution getJudgedBySolutionId(int solutionId);        // 获取单条 solution

    int add(Solution solution);

    int updateState(Solution solution);     // 更新状态

    int updateResult(Solution solution);    // 更新结果

    int updatePassRate(Solution solution);  // 更新通过率
}

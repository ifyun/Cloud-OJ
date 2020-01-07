package top.cloudli.judgeservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.judgeservice.model.Solution;

import java.util.List;

@Mapper
public interface SolutionDao {
    List<Solution> getSubmitted();  // 获取已提交的答案

    List<Solution> getCompiled();   // 获取已经过编译的答案

    List<Solution> getJudged();     // 获取已经过判题的答案

    int updateState(int id, int state);     // 更新状态

    int updateResult(int id, int result);   // 更新结果
}

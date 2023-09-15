package cloud.oj.judge.dao;

import cloud.oj.judge.entity.Solution;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SolutionDao {
    /**
     * 从记录找出指定题目的历史最高分
     *
     * @param uid       用户 Id
     * @param problemId 题目 Id
     * @param contestId 竞赛 Id，可为空
     * @return 分数
     */
    Double getMaxScoreOfUser(Integer uid, Integer problemId, Integer contestId);

    void create(Solution solution);

    /**
     * 更新状态
     *
     * @param state {@link cloud.oj.judge.constant.State}
     */
    void updateState(Integer solutionId, Integer state);

    /**
     * 更新判题结果
     *
     * @param solution {@link Solution}
     */
    void updateWithResult(Solution solution);
}

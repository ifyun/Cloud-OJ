package cloud.oj.judge.dao;

import cloud.oj.judge.entity.Solution;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SolutionDao {
    /**
     * 从记录找出指定题目的历史最高分
     *
     * @param userId    用户名
     * @param problemId 题目 Id
     * @param contestId 竞赛 Id，可为空
     * @return 分数
     */
    Double getMaxScoreOfUser(String userId, Integer problemId, Integer contestId);

    void add(Solution solution);

    /**
     * 更新状态，有以下字段：
     *
     * <ul>
     *     <li>state</li>
     *     <li>result</li>
     *     <li>passRate</li>
     *     <li>score</li>
     * </ul>
     *
     * @param solution {@link Solution}
     */
    void updateState(Solution solution);
}

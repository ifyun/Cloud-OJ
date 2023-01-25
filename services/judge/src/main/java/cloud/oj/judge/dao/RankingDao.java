package cloud.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RankingDao {
    /**
     * 提交次数 +1
     *
     * @param userId 用户名
     */
    void incCommitted(String userId);

    /**
     * 提交次数 +1（竞赛）
     *
     * @param contestId 竞赛 Id
     * @param userId    用户名
     */
    void incCommittedForContest(int contestId, String userId);

    /**
     * 更新排名
     *
     * @param userId 用户名
     * @param time   本次提交的时间
     */
    void update(String userId, Long time);

    /**
     * 更新排名（竞赛）
     *
     * @param contestId 竞赛 Id
     * @param userId    用户名
     * @param time      本次提交的时间
     */
    void updateForContest(int contestId, String userId, Long time);
}

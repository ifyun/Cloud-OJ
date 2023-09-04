package cloud.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RankingDao {
    /**
     * 提交次数 +1
     *
     * @param uid  用户 Id
     * @param time 更新时间
     */
    void incCommitted(Integer uid, Long time);

    /**
     * 提交次数 +1（竞赛）
     *
     * @param uid       用户 Id
     * @param contestId 竞赛 Id
     * @param time      更新时间
     */
    void incCommittedForContest(Integer uid, Integer contestId, Long time);

    /**
     * 更新排名
     *
     * @param uid  用户 Id
     * @param time 本次提交的时间
     */
    void update(Integer uid, Long time);

    /**
     * 更新排名（竞赛）
     *
     * @param contestId 竞赛 Id
     * @param uid       用户 Id
     * @param time      本次提交的时间
     */
    void updateForContest(Integer contestId, Integer uid, Long time);
}

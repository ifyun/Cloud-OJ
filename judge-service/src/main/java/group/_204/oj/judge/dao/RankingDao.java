package group._204.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface RankingDao {
    void update(String userId, Date time);

    void updateContest(int contestId, String userId, Date time);
}

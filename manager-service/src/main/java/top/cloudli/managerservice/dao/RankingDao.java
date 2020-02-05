package top.cloudli.managerservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.managerservice.model.Ranking;

import java.util.List;

@Mapper
public interface RankingDao {
    long getCount();

    List<Ranking> getRanking(int start, int limit);

    long getContestRankingCount(int contestId);

    List<Ranking> getContestRanking(int contestId, int start, int limit);
}

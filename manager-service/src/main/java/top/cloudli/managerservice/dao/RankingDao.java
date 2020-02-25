package top.cloudli.managerservice.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RankingDao {

    List<List<?>> getRanking(int start, int limit);

    List<List<?>> getContestRanking(int contestId, int start, int limit);
}

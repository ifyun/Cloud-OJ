package cloud.oj.core.dao;

import cloud.oj.core.entity.JudgeResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RankingDao {

    List<List<?>> getRanking(int start, int limit);

    List<List<?>> getContestRanking(int contestId, int start, int limit);

    List<JudgeResult> getDetail(int contestId, String userId);
}

package cloud.oj.core.dao;

import cloud.oj.core.entity.Ranking;
import cloud.oj.core.entity.ScoreDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RankingDao {

    List<List<?>> get(int start, int limit);

    List<Ranking> getForContest(Integer contestId);

    List<ScoreDetail> getDetail(Integer uid, Integer cid);

    int deleteByUser(Integer uid);
}

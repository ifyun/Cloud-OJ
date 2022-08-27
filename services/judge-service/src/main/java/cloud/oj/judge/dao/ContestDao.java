package cloud.oj.judge.dao;

import cloud.oj.judge.model.Contest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContestDao {
    Contest getContest(Integer contestId);
}

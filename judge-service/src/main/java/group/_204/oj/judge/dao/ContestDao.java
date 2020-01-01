package group._204.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;
import group._204.oj.judge.model.Contest;

@Mapper
public interface ContestDao {
    Contest getContest(Integer contestId);
}

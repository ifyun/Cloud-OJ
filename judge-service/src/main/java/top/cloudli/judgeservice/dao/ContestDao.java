package top.cloudli.judgeservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.judgeservice.model.Contest;

@Mapper
public interface ContestDao {
    Contest getContest(Integer contestId);
}

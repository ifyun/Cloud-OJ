package group._204.oj.manager.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JudgeResultDao {
    List<List<?>> getHistoryByUserId(String userId, int start, int limit);
}

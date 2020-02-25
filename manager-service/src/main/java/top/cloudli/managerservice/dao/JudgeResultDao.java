package top.cloudli.managerservice.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JudgeResultDao {
    List<List<?>> getByUserId(String userId, int start, int limit);
}

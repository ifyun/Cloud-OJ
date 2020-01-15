package top.cloudli.managerservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.managerservice.model.JudgeResult;

import java.util.List;

@Mapper
public interface JudgeResultDao {
    long getCount(String userId);
    List<JudgeResult> getByUserId(String userId, int start, int limit);
}

package top.cloudli.judgeservice.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskDao {
    String getUUID(String taskName);
    void setUUID(String taskName, String uuid);
}

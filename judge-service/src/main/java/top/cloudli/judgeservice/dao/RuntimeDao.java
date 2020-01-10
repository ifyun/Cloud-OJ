package top.cloudli.judgeservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.judgeservice.model.Runtime;

@Mapper
public interface RuntimeDao {
    int add(Runtime runtime);
    int update(Runtime runtime);
    Long getTimeout(int problemId);
}

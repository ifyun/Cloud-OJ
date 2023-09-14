package cloud.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SourceDao {
    void create(Integer solutionId, String source);
}

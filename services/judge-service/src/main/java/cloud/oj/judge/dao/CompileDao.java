package cloud.oj.judge.dao;

import cloud.oj.judge.entity.Compile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompileDao {
    void get(int solutionId);
    void add(Compile compile);
}

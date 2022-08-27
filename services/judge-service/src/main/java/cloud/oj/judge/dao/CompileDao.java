package cloud.oj.judge.dao;

import cloud.oj.judge.model.Compile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompileDao {
    int get(int solutionId);
    int add(Compile compile);
}

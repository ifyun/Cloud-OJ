package group._204.oj.judge.dao;

import group._204.oj.judge.model.Compile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompileDao {
    int get(int solutionId);
    int add(Compile compile);
}

package top.cloudli.judgeservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.judgeservice.model.Compile;

@Mapper
public interface CompileDao {
    int get(int solutionId);
    int add(Compile compile);
}

package top.cloudli.judgeservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.judgeservice.model.SourceCode;

@Mapper
public interface SourceCodeDao {
    SourceCode get(int solutionId);
    int add(SourceCode sourceCode);
}

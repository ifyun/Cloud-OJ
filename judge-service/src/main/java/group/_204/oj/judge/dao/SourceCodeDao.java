package group._204.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;
import group._204.oj.judge.model.SourceCode;

@Mapper
public interface SourceCodeDao {
    SourceCode get(String solutionId);
    int add(SourceCode sourceCode);
}

package cloud.oj.judge.dao;

import cloud.oj.judge.model.SourceCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SourceCodeDao {
    SourceCode get(String solutionId);
    int add(SourceCode sourceCode);
}

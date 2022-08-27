package cloud.oj.judge.dao;

import cloud.oj.judge.entity.SourceCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SourceCodeDao {
    SourceCode get(String solutionId);
    void add(SourceCode sourceCode);
}

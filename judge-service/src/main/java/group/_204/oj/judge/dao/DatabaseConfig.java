package group._204.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DatabaseConfig {
    void disableFKChecks();
}

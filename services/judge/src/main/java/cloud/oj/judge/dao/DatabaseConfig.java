package cloud.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DatabaseConfig {
    @Select("set foreign_key_checks = 0")
    void disableFKChecks();
}

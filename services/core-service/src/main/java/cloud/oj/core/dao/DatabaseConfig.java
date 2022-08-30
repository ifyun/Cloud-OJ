package cloud.oj.core.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DatabaseConfig {
    @Select("set time_zone = #{tz}")
    void setTimezone(String tz);
}

package cloud.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SettingsDao {
    @Select("select auto_del_solutions from settings where id = 0")
    boolean isAutoDelSolutions();
}

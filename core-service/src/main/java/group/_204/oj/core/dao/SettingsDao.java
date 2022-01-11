package group._204.oj.core.dao;

import group._204.oj.core.model.Settings;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SettingsDao {
    Settings get();

    int update(Settings settings);

    int createDefault();
}

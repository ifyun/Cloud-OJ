package cloud.oj.core.dao;

import cloud.oj.core.entity.Settings;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SettingsDao {
    Settings get();

    int update(Settings settings);
}

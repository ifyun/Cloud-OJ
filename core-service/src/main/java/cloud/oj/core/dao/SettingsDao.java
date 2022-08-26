package cloud.oj.core.dao;

import cloud.oj.core.model.Settings;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SettingsDao {
    Settings get();

    int update(Settings settings);

    int createDefault();
}

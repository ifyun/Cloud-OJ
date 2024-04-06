package cloud.oj.core.dao;

import cloud.oj.core.entity.Settings;

public interface SettingsDao {
    Settings get();

    int update(Settings settings);
}

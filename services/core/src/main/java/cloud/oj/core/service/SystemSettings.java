package cloud.oj.core.service;

import cloud.oj.core.dao.SettingsDao;
import cloud.oj.core.entity.Settings;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SystemSettings {

    private final SettingsDao settingsDao;

    @Autowired
    public SystemSettings(SettingsDao settingsDao) {
        this.settingsDao = settingsDao;
    }

    public boolean setSettings(Settings settings) {
        return settingsDao.update(settings) > 0;
    }

    public Settings getSettings() {
        return settingsDao.get();
    }
}

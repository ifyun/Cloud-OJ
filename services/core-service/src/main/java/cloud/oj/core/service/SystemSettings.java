package cloud.oj.core.service;

import cloud.oj.core.dao.SettingsDao;
import cloud.oj.core.model.Settings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Service
public class SystemSettings {
    private SystemSettings self;
    private Settings settings;

    @Resource
    private SettingsDao settingsDao;

    @Autowired
    public void setSelf(SystemSettings systemSettings) {
        this.self = systemSettings;
    }

    @PostConstruct
    public void init() {
        self.check();
    }

    @Transactional
    public void check() {
        if (settingsDao.get() == null) {
            if (settingsDao.createDefault() == 0) {
                log.error("Cannot create default system settings.");
            } else {
                log.info("Created default system settings.");
            }
        }

        settings = settingsDao.get();
    }

    public boolean setSettings(Settings settings) {
        return settingsDao.update(settings) > 0;
    }

    public Settings getSettings() {
        return settings;
    }
}

package cloud.oj.core.service;

import cloud.oj.core.dao.SettingsDao;
import cloud.oj.core.entity.Settings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SystemSettings {

    private Settings settings;

    private final SettingsDao settingsDao;

    public SystemSettings(SettingsDao settingsDao) {
        this.settingsDao = settingsDao;
    }

    public HttpStatus setSettings(Settings settings) {
        if (settingsDao.update(settings) > 0) {
            this.settings = settings;
            return HttpStatus.OK;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public Settings getSettings() {
        if (settings == null) {
            settings = settingsDao.get();
        }

        return settings;
    }
}

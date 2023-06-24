package cloud.oj.core.service;

import cloud.oj.core.dao.SettingsDao;
import cloud.oj.core.entity.Settings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SystemSettings {

    private final SettingsDao settingsDao;

    @Autowired
    public SystemSettings(SettingsDao settingsDao) {
        this.settingsDao = settingsDao;
    }

    public HttpStatus setSettings(Settings settings) {
        return settingsDao.update(settings) > 0 ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public Settings getSettings() {
        return settingsDao.get();
    }
}

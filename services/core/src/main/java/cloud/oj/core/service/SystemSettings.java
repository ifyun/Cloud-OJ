package cloud.oj.core.service;

import cloud.oj.core.entity.Settings;
import cloud.oj.core.repo.SettingsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemSettings {

    private final SettingsRepo settingsRepo;

    public Settings getSettings() {
        return settingsRepo.select();
    }

    public HttpStatus setSettings(Settings settings) {
        return settingsRepo.update(settings) > 0 ?
                HttpStatus.OK :
                HttpStatus.INTERNAL_SERVER_ERROR;
    }
}

package cloud.oj.core.service;

import cloud.oj.core.entity.Settings;
import cloud.oj.core.error.GenericException;
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

    public void setSettings(Settings settings) {
        if (settingsRepo.update(settings) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "操作失败");
        }
    }
}

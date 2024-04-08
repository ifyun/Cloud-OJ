package cloud.oj.core.service;

import cloud.oj.core.entity.Settings;
import cloud.oj.core.repo.SettingsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SystemSettings {

    private final SettingsRepo settingsRepo;

    public Mono<Settings> getSettings() {
        return settingsRepo.select();
    }

    public Mono<HttpStatus> setSettings(Settings settings) {
        return settingsRepo.update(settings)
                .map(rows -> rows > 0 ?
                        HttpStatus.OK :
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
    }
}

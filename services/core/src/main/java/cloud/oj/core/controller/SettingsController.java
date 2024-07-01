package cloud.oj.core.controller;

import cloud.oj.core.entity.Settings;
import cloud.oj.core.service.SystemSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SystemSettings systemSettings;

    /**
     * 获取系统设置
     */
    @GetMapping
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok(systemSettings.getSettings());
    }

    /**
     * 更新系统设置
     */
    @PutMapping(consumes = "application/json")
    public ResponseEntity<?> updateSettings(@RequestBody Settings settings) {
        systemSettings.setSettings(settings);
        return ResponseEntity.ok().build();
    }
}

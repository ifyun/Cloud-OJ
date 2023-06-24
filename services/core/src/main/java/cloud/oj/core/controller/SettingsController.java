package cloud.oj.core.controller;

import cloud.oj.core.entity.Settings;
import cloud.oj.core.service.SystemSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("settings")
public class SettingsController {

    private final SystemSettings systemSettings;

    @Autowired
    public SettingsController(SystemSettings systemSettings) {
        this.systemSettings = systemSettings;
    }

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
        return ResponseEntity.status(systemSettings.setSettings(settings)).build();
    }
}

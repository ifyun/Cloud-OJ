package cloud.oj.core.controller;

import cloud.oj.core.entity.Settings;
import cloud.oj.core.service.SystemSettings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("settings")
public class SettingsController {

    @Resource
    private SystemSettings systemSettings;

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
        return systemSettings.setSettings(settings) ?
                ResponseEntity.ok().build() : ResponseEntity.status(500).build();
    }
}

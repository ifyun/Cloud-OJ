package group._204.oj.core.controller;

import group._204.oj.core.service.SystemSettings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("settings")
public class SystemSettingsController {

    @Resource
    private SystemSettings systemSettings;

    /**
     * 获取系统设置
     */
    @GetMapping()
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok(systemSettings.getConfig());
    }

    /**
     * 更新系统设置
     */
    @PutMapping(consumes = "application/json")
    public ResponseEntity<?> updateSettings(@RequestBody SystemSettings.Config config) {
        return systemSettings.setConfig(config) ?
                ResponseEntity.ok().build() : ResponseEntity.status(500).build();
    }
}

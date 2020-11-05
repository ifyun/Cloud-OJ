package group._204.oj.manager.controller;

import group._204.oj.manager.service.SystemSettings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("settings")
public class SystemSettingsController {

    @Resource
    private SystemSettings systemSettings;

    @GetMapping("")
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok(systemSettings.getConfig());
    }

    @PutMapping("")
    public ResponseEntity<?> updateSettings(@RequestBody SystemSettings.Config config) {
        return systemSettings.setConfig(config) ?
                ResponseEntity.ok().build() : ResponseEntity.status(500).build();
    }
}

package group._204.oj.manager.controller;

import group._204.oj.manager.service.SystemSettings;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("settings")
@Api(tags = "系统设置")
public class SystemSettingsController {

    @Resource
    private SystemSettings systemSettings;

    @ApiOperation(value = "获取系统设置", notes = "需要 ROOT 权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = SystemSettings.Config.class)
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok(systemSettings.getConfig());
    }

    @ApiOperation(value = "更新系统设置", notes = "需要 ROOT 权限")
    @ApiImplicitParam(name = "config", dataTypeClass = SystemSettings.Config.class, required = true)
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功")
    })
    @PutMapping(consumes = "application/json")
    public ResponseEntity<?> updateSettings(@RequestBody SystemSettings.Config config) {
        return systemSettings.setConfig(config) ?
                ResponseEntity.ok().build() : ResponseEntity.status(500).build();
    }
}

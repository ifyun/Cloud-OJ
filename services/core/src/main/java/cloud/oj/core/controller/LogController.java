package cloud.oj.core.controller;

import cloud.oj.core.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 日志 API
 */
@RestController
@RequestMapping("log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping
    public ResponseEntity<?> getLatest10Logs(@RequestParam(required = false) Long time) {
        var data = logService.getLatest10(time);
        return data.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(data);
    }

    @GetMapping(path = "range")
    public ResponseEntity<List<String>> getRangeLogs(Long start, Long end) {
        var data = logService.getRange(start, end);
        return data.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(data);
    }
}

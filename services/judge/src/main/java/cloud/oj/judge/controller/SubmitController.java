package cloud.oj.judge.controller;

import cloud.oj.judge.entity.SubmitData;
import cloud.oj.judge.service.SubmitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提交代码接口
 */
@RestController
public class SubmitController {

    private final SubmitService submitService;

    public SubmitController(SubmitService submitService) {
        this.submitService = submitService;
    }

    /**
     * 提交代码，普通用户
     */
    @PostMapping("submit")
    public ResponseEntity<?> submit(@RequestHeader Integer uid, @RequestBody SubmitData data) {
        data.setUid(uid);
        var time = submitService.submitCode(data, false);
        return ResponseEntity.accepted().body(time);
    }

    /**
     * 提交代码，管理员
     */
    @PostMapping("admin/submit")
    public ResponseEntity<?> adminSubmit(@RequestHeader Integer uid, @RequestBody SubmitData data) {
        data.setUid(uid);
        var time = submitService.submitCode(data, true);
        return ResponseEntity.accepted().body(time);
    }
}

package cloud.oj.judge.controller;

import cloud.oj.judge.entity.SubmitData;
import cloud.oj.judge.service.SubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提交代码接口
 */
@Slf4j
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
    public ResponseEntity<?> submit(@RequestBody SubmitData data) {
        return submitService.submitCode(data, false);
    }

    /**
     * 提交代码，管理员
     */
    @PostMapping("admin/submit")
    public ResponseEntity<?> adminSubmit(@RequestBody SubmitData data) {
        return submitService.submitCode(data, true);
    }
}

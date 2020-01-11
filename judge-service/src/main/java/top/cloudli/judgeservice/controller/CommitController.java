package top.cloudli.judgeservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.judgeservice.data.CommitData;
import top.cloudli.judgeservice.model.JudgeResult;
import top.cloudli.judgeservice.service.CommitService;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 提交代码接口
 */
@RestController
@RequestMapping("/commit")
public class CommitController {

    @Resource
    private CommitService commitService;

    @PostMapping("")
    public ResponseEntity<?> commitCode(@RequestBody CommitData data) {
        CompletableFuture<JudgeResult> future = commitService.commit(data);
        try {
            return ResponseEntity.ok(future.get());
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.accepted().body("正在处理.");
        }
    }
}

package top.cloudli.judgeservice.controller;

import org.springframework.web.bind.annotation.*;
import top.cloudli.judgeservice.data.CommitData;
import top.cloudli.judgeservice.data.Result;
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
    public Result commitCode(@RequestBody CommitData data) {
        CompletableFuture<JudgeResult> future = commitService.commit(data);
        try {
            return new Result(future.get());
        } catch (InterruptedException | ExecutionException e) {
            return new Result(201, "暂时没有结果, 请刷新页面.", e.getMessage());
        }
    }
}

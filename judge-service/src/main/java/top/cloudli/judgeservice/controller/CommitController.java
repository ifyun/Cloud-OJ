package top.cloudli.judgeservice.controller;

import org.springframework.web.bind.annotation.*;
import top.cloudli.judgeservice.data.CommitData;
import top.cloudli.judgeservice.data.Result;
import top.cloudli.judgeservice.service.CommitService;

import javax.annotation.Resource;

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
        return new Result(commitService.commit(data));
    }

    @GetMapping("")
    public Result getJudgeResult(int solutionId) {
        // TODO 返回判题结果
        return null;
    }
}

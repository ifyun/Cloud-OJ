package top.cloudli.judgeservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.judgeservice.dao.ContestDao;
import top.cloudli.judgeservice.data.CommitData;
import top.cloudli.judgeservice.model.Contest;
import top.cloudli.judgeservice.model.JudgeResult;
import top.cloudli.judgeservice.service.CommitService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 提交代码接口
 */
@Slf4j
@RestController
@RequestMapping("/commit")
public class CommitController {

    @Resource
    private CommitService commitService;

    @Resource
    private ContestDao contestDao;

    @PostMapping("")
    public ResponseEntity<?> commitCode(@RequestBody CommitData data) {
        Integer contestId = data.getContestId();
        if (contestId != null) {
            Contest contest = contestDao.getContest(contestId);
            if (contest.getEndAt().before(new Date()))
                return ResponseEntity.ok("当前竞赛/作业已结束！");
        }

        CompletableFuture<JudgeResult> future = commitService.commit(data);
        try {
            JudgeResult result = future.get();
            return ResponseEntity.ok(result != null ? result : "你的答案已提交，若等待时间过长，可到提交记录查看结果");
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
            return ResponseEntity.accepted().body(e.getMessage());
        }
    }
}

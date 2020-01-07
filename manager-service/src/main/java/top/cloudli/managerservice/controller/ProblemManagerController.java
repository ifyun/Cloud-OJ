package top.cloudli.managerservice.controller;

import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.dao.ProblemDao;
import top.cloudli.managerservice.model.Problem;
import top.cloudli.managerservice.model.Result;

import javax.annotation.Resource;

/**
 * 题目管理接口
 */
@RestController
@RequestMapping("/problem")
public class ProblemManagerController {

    @Resource
    private ProblemDao problemDao;

    @GetMapping("")
    public Result getProblems() {
        return new Result(problemDao.getAll());
    }

    @GetMapping("search/{keyword}")
    public Result searchProblems(@PathVariable String keyword) {
        return new Result(problemDao.searchByTitle(String.format("%%%s%%", keyword)));
    }

    @PutMapping("")
    public Result updateProblem(@RequestBody Problem problem) {
        return new Result(problemDao.update(problem));
    }

    @PostMapping("")
    public Result addProblem(@RequestBody Problem problem) {
        return new Result(problemDao.add(problem));
    }

    @DeleteMapping("{problemId}")
    public Result deleteProblem(@PathVariable int problemId) {
        return new Result(problemDao.delete(problemId));
    }
}

package top.cloudli.managerservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.cloudli.managerservice.dao.ProblemDao;
import top.cloudli.managerservice.model.Problem;

import javax.annotation.Resource;
import java.util.List;

/**
 * 题目管理接口
 */
@RestController
@RequestMapping("/problem")
public class ProblemManagerController {

    @Resource
    private ProblemDao problemDao;

    @GetMapping("")
    public ResponseEntity<List<Problem>> getProblems() {
        List<Problem> problems = problemDao.getAll();
        return problems.size() != 0 ?
                ResponseEntity.ok(problems) :
                ResponseEntity.noContent().build();
    }

    @GetMapping("search/{keyword}")
    public ResponseEntity<List<Problem>> searchProblems(@PathVariable String keyword) {
        List<Problem> problems = problemDao.searchByTitle(String.format("%%%s%%", keyword));
        return problems.size() != 0 ?
                ResponseEntity.ok(problems) :
                ResponseEntity.noContent().build();
    }

    @PutMapping("")
    public ResponseEntity<Void> updateProblem(@RequestBody Problem problem) {
        return problemDao.update(problem) == 1 ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @PostMapping("")
    public ResponseEntity<String> addProblem(@RequestBody Problem problem) {
        return problemDao.add(problem) == 1 ?
                ResponseEntity.status(HttpStatus.CREATED).body("添加成功.") :
                ResponseEntity.badRequest().body("添加失败.");
    }

    @DeleteMapping("{problemId}")
    public ResponseEntity<Void> deleteProblem(@PathVariable int problemId) {
        return problemDao.delete(problemId) == 1 ?
                ResponseEntity.noContent().build() :
                ResponseEntity.status(HttpStatus.GONE).build();
    }
}

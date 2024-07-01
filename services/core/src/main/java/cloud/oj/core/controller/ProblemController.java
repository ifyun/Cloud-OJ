package cloud.oj.core.controller;

import cloud.oj.core.entity.DataConf;
import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 题目管理接口
 */
@RestController
@RequestMapping("problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    /**
     * 获取所有开放的题目
     */
    @GetMapping
    public ResponseEntity<PageData<Problem>> getAllEnable(@RequestHeader(required = false) Integer uid,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "15") int size,
                                                          String keyword) {
        var data = problemService.getAllEnabled(uid, keyword, page, size);
        return data.getTotal() > 0 ?
                ResponseEntity.ok(data) :
                ResponseEntity.noContent().build();
    }

    /**
     * 获取所有题目
     */
    @GetMapping(path = "admin")
    public ResponseEntity<PageData<Problem>> getAll(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "15") int size,
                                                    String keyword) {
        var data = problemService.getAll(keyword, page, size);
        return data.getTotal() > 0 ?
                ResponseEntity.ok(data) :
                ResponseEntity.noContent().build();
    }

    /**
     * 获取单个题目(仅开放)
     */
    @GetMapping(path = "{pid}")
    public ResponseEntity<Problem> getEnabled(@PathVariable Integer pid) {
        return ResponseEntity.ok(problemService.getSingleEnabled(pid));
    }

    /**
     * 获取单个题目(包括未开放)
     */
    @GetMapping(path = "admin/{pid}")
    public ResponseEntity<Problem> getSingle(@PathVariable Integer pid) {
        return ResponseEntity.ok(problemService.getSingle(pid));
    }

    /**
     * 更新题目
     */
    @PutMapping(path = "admin")
    public ResponseEntity<?> update(@RequestBody Problem problem) {
        problemService.update(problem);
        return ResponseEntity.ok().build();
    }

    /**
     * 设置题目开放/关闭状态
     */
    @PutMapping(path = "admin/{pid}")
    public ResponseEntity<?> setEnable(@PathVariable Integer pid, Boolean enable) {
        problemService.setEnable(pid, enable);
        return ResponseEntity.ok().build();
    }

    /**
     * 添加题目
     */
    @PostMapping(path = "admin", consumes = "application/json")
    public ResponseEntity<?> add(@RequestBody Problem problem) {
        problemService.create(problem);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新测试数据配置
     */
    @PostMapping(path = "admin/data_conf", consumes = "application/json")
    public ResponseEntity<?> saveDataConf(@RequestBody DataConf conf) {
        problemService.saveDataConf(conf);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除题目
     */
    @DeleteMapping(path = "admin/{pid}")
    public ResponseEntity<?> delete(@PathVariable Integer pid) {
        return ResponseEntity.status(problemService.delete(pid)).build();
    }
}

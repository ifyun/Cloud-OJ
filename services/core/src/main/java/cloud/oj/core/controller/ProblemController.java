package cloud.oj.core.controller;

import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.service.ProblemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 题目管理接口
 */
@RestController
@RequestMapping("problem")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    /**
     * 获取所有开放的题目
     */
    @GetMapping
    public Mono<ResponseEntity<PageData<Problem>>> getAllEnable(String keyword,
                                                                @RequestHeader(required = false) Integer uid,
                                                                @RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "15") int size) {
        return problemService.getAllEnabled(uid, keyword, page, size)
                .map(data -> data.getTotal() > 0 ?
                        ResponseEntity.ok(data) :
                        ResponseEntity.noContent().build()
                );
    }

    /**
     * 获取所有题目
     */
    @GetMapping(path = "admin")
    public Mono<ResponseEntity<PageData<Problem>>> getAll(String keyword,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "15") int size) {
        return problemService.getAll(keyword, page, size)
                .map(data -> data.getTotal() > 0 ?
                        ResponseEntity.ok(data) :
                        ResponseEntity.noContent().build()
                );
    }

    /**
     * 获取单个题目(仅开放)
     */
    @GetMapping(path = "{pid}")
    public Mono<ResponseEntity<Problem>> getEnable(@PathVariable Integer pid) {
        return problemService.getSingleEnabled(pid)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * 获取单个题目(包括未开放)
     */
    @GetMapping(path = "admin/{pid}")
    public Mono<ResponseEntity<Problem>> getSingle(@PathVariable Integer pid) {
        return problemService.getSingle(pid)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * 更新题目
     */
    @PutMapping(path = "admin")
    public Mono<ResponseEntity<?>> update(@RequestBody Problem problem) {
        return problemService.update(problem)
                .map(status -> ResponseEntity.status(status).build());
    }

    /**
     * 设置题目开放/关闭状态
     */
    @PutMapping(path = "admin/{pid}")
    public Mono<ResponseEntity<?>> setEnable(@PathVariable Integer pid, Boolean enable) {
        return problemService.setEnable(pid, enable)
                .map(status -> ResponseEntity.status(status).build());
    }

    /**
     * 添加题目
     */
    @PostMapping(path = "admin", consumes = "application/json")
    public Mono<ResponseEntity<?>> add(@RequestBody Problem problem) {
        return problemService.create(problem)
                .map(status -> ResponseEntity.status(status).build());
    }

    /**
     * 删除题目
     */
    @DeleteMapping(path = "admin/{pid}")
    public Mono<ResponseEntity<?>> delete(@PathVariable Integer pid) {
        return problemService.delete(pid)
                .map(status -> ResponseEntity.status(status).build());
    }
}

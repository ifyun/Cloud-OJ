package cloud.oj.core.controller;

import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Solution;
import cloud.oj.core.service.SolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 提交记录/判题结果接口
 */
@RestController
@RequestMapping("solution")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    /**
     * 根据过滤条件获取提交记录
     *
     * @param filter      1: by problemId, 2: by title
     * @param filterValue problemId/title
     */
    @GetMapping
    public Mono<ResponseEntity<PageData<Solution>>> getAll(@RequestHeader Integer uid,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "15") int limit,
                                                           Integer filter,
                                                           String filterValue) {
        return solutionService.getSolutionsByUidAndFilter(uid, page, limit, filter, filterValue)
                .map(data -> data.getTotal() > 0 ?
                        ResponseEntity.ok(data) :
                        ResponseEntity.noContent().build()
                );
    }

    /**
     * 获取判题结果
     */
    @GetMapping("time/{time}")
    public Flux<ServerSentEvent<String>> getByUidAndTime(@RequestHeader Integer uid, @PathVariable Long time) {
        return solutionService.getSolutionByUidAndTime(uid, time);
    }

    @GetMapping("{sid}")
    public Mono<ResponseEntity<Solution>> getBySolutionByUser(@RequestHeader Integer uid, @PathVariable Integer sid) {
        return solutionService.getSolutionByUser(uid, sid)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }
}

package cloud.oj.core.controller;

import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Solution;
import cloud.oj.core.service.SolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
     */
    @GetMapping
    public ResponseEntity<PageData<Solution>> getAll(@RequestHeader Integer uid,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "15") int limit,
                                                     Integer filter,
                                                     String filterValue) {
        var data = solutionService.getSolutionsByUidAndFilter(uid, page, limit, filter, filterValue);
        return data.getTotal() > 0 ?
                ResponseEntity.ok(data) :
                ResponseEntity.noContent().build();
    }

    /**
     * 获取判题结果
     */
    @GetMapping("time/{time}")
    public SseEmitter getByUidAndTime(@RequestHeader Integer uid, @PathVariable Long time) {
        return solutionService.getSolutionByUidAndTime(uid, time);
    }

    @GetMapping("{sid}")
    public ResponseEntity<Solution> getBySolutionByUser(@RequestHeader Integer uid, @PathVariable Integer sid) {
        return ResponseEntity.ok(solutionService.getSolutionByUser(uid, sid));
    }
}

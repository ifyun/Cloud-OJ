package cloud.oj.core.controller;

import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Solution;
import cloud.oj.core.entity.SolutionFilter;
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
     * (User) 根据过滤条件获取提交记录
     */
    @GetMapping
    public ResponseEntity<PageData<Solution>> getAll(@RequestHeader Integer uid,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "15") int size,
                                                     Integer filter,
                                                     String filterValue) {
        var data = solutionService.getSolutionsByUidAndFilter(uid, page, size, filter, filterValue);
        return data.getTotal() > 0 ?
                ResponseEntity.ok(data) :
                ResponseEntity.noContent().build();
    }

    /**
     * (Admin) 根据过滤条件获取提交记录
     */
    @RequestMapping(path = "admin/queries", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<PageData<Solution>> getAllByFilter(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestBody(required = false) SolutionFilter filter
    ) {
        var data = solutionService.getSolutionsByAdmin(filter, page, size);
        return data.getTotal() > 0 ?
                ResponseEntity.ok(data) :
                ResponseEntity.noContent().build();
    }

    @GetMapping("admin/{sid}")
    public ResponseEntity<Solution> getSolutionById(@PathVariable String sid) {
        return ResponseEntity.ok(solutionService.getSolutionById(sid));
    }

    /**
     * 获取判题结果
     */
    @GetMapping("time/{time}")
    public SseEmitter getByUidAndTime(@RequestHeader Integer uid, @PathVariable Long time) {
        return solutionService.getSolutionByUidAndTime(uid, time);
    }

    @GetMapping("{sid}")
    public ResponseEntity<Solution> getBySolutionByUser(@RequestHeader Integer uid, @PathVariable String sid) {
        return ResponseEntity.ok(solutionService.getSolutionByUser(uid, sid));
    }
}

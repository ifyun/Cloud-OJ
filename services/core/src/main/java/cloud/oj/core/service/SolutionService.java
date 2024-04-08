package cloud.oj.core.service;

import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Solution;
import cloud.oj.core.repo.CommonRepo;
import cloud.oj.core.repo.SolutionRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionService {

    private static final int MAX_COUNT = 180;

    private final CommonRepo commonRepo;

    private final SolutionRepo solutionRepo;

    private final SystemSettings systemSettings;

    private final ObjectMapper objectMapper;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Mono<PageData<Solution>> getSolutionsByUidAndFilter(Integer uid, Integer page, Integer size, Integer filter, String filterValue) {
        return solutionRepo.selectAllByUid(uid, page, size, filter, filterValue)
                .collectList()
                .zipWith(commonRepo.selectFoundRows())
                .map(t -> new PageData<>(t.getT1(), t.getT2()));
    }

    /**
     * 根据 id 获取提交
     * <p>隔离级别：读未提交</p>
     *
     * @return {@link ServerSentEvent}
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Flux<ServerSentEvent<String>> getSolutionByUidAndTime(Integer uid, Long time) {
        return Flux.interval(Duration.ofMillis(500))
                .takeUntil(count -> count == MAX_COUNT)
                .flatMap(count -> systemSettings.getSettings()
                        .flatMap(settings -> solutionRepo.selectByUidAndTime(uid, time, settings.isShowPassedPoints()))
                        .map(solution -> {
                            try {
                                var event = solution.getState() == 0 ? "complete" : "message";
                                return ServerSentEvent.<String>builder()
                                        .id(count.toString())
                                        .event(event)
                                        .data(objectMapper.writeValueAsString(solution))
                                        .build();
                            } catch (JsonProcessingException e) {
                                log.error(e.getMessage());
                                return ServerSentEvent.<String>builder()
                                        .id(count.toString())
                                        .event("error")
                                        .data(e.getMessage())
                                        .build();
                            }
                        })
                );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<Solution> getSolutionByUser(Integer uid, Integer sid) {
        return systemSettings.getSettings()
                .flatMap(settings -> solutionRepo.selectByUidAndSid(uid, sid, settings.isShowPassedPoints()))
                .zipWith(solutionRepo.selectSourceCode(sid))
                .map(t -> {
                    var s = t.getT1();
                    s.setSourceCode(t.getT2());
                    return s;
                });
    }
}

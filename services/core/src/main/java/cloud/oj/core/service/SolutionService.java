package cloud.oj.core.service;

import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Solution;
import cloud.oj.core.error.GenericException;
import cloud.oj.core.repo.CommonRepo;
import cloud.oj.core.repo.SolutionRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionService {

    private static final int MAX_COUNT = 180;

    private final PlatformTransactionManager transactionManager;

    private final CommonRepo commonRepo;

    private final SolutionRepo solutionRepo;

    private final SystemSettings systemSettings;

    private final ObjectMapper objectMapper;

    /**
     * 根据 uid 和过滤条件查询提交
     *
     * <p>隔离级别：读未提交</p>
     *
     * @param uid         用户 Id
     * @param page        页数
     * @param size        每页数量
     * @param filter      过滤条件，1 -> pid，2 -> title
     * @param filterValue 过滤值
     * @return {@link PageData} of {@link Solution}
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public PageData<Solution> getSolutionsByUidAndFilter(Integer uid, Integer page, Integer size,
                                                         Integer filter, String filterValue) {
        var data = solutionRepo.selectAllByUid(uid, page, size, filter, filterValue);
        var total = commonRepo.selectFoundRows();
        return new PageData<>(data, total);
    }

    /**
     * 根据 uid 和提交时间查询提交
     *
     * <p>隔离级别：读未提交</p>
     * <p>开启了新的线程，不可使用声明式事务</p>
     *
     * @return {@link ServerSentEvent}
     */
    public SseEmitter getSolutionByUidAndTime(Integer uid, Long time) {
        var emitter = new SseEmitter(0L);
        var executor = Executors.newSingleThreadExecutor();
        var settings = systemSettings.getSettings();

        executor.execute(() -> {
            var count = 0;
            var def = new DefaultTransactionDefinition();

            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
            var status = transactionManager.getTransaction(def);

            try {
                while (true) {
                    TimeUnit.MILLISECONDS.sleep(500);
                    var result = solutionRepo.selectByUidAndTime(uid, time, settings.isShowPassedPoints());

                    if (result.isEmpty()) {
                        count += 1;
                        continue;
                    }

                    var event = SseEmitter.event()
                            .name("message")
                            .data(objectMapper.writeValueAsString(result.get()));

                    emitter.send(event);
                    count += 1;

                    if (result.get().getState() == 0) {
                        // 题解已判，结束 Event
                        emitter.complete();
                        break;
                    }

                    if (count == MAX_COUNT) {
                        var error = SseEmitter.event()
                                .name("error")
                                .data("轮询超时");
                        emitter.send(error);
                        emitter.complete();
                        break;
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            transactionManager.commit(status);
        });

        return emitter;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Solution getSolutionByUser(Integer uid, String sid) {
        var settings = systemSettings.getSettings();
        var solution = solutionRepo.selectByUidAndSid(uid, sid, settings.isShowPassedPoints());
        var source = solutionRepo.selectSourceCode(sid);

        solution.ifPresent(s -> source.ifPresent(s::setSourceCode));

        if (solution.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "找不到提交记录");
        }

        return solution.get();
    }
}

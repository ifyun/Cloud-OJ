package cloud.oj.core.service;

import cloud.oj.core.dao.SolutionDao;
import cloud.oj.core.entity.Solution;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionService {
    private static final int MAX_COUNT = 180;

    private final SolutionDao solutionDao;

    private final SystemSettings systemSettings;

    private final ObjectMapper objectMapper;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<List<?>> getSolutions(Integer uid, Integer page, Integer limit, Integer filter, String filterValue) {
        return solutionDao.getSolutionsByUser(uid, (page - 1) * limit, limit, filter, filterValue);
    }

    /**
     * 根据 id 获取提交
     * <p>隔离级别：读未提交</p>
     * <p>开启了新的线程，不可使用声明式事务，隔离级别在 SQL 语句中设置</p>
     *
     * @return {@link SseEmitter}
     */
    public SseEmitter getSolutionByUidAndTime(Integer uid, Long time) {
        var emitter = new SseEmitter(0L);
        var executor = Executors.newSingleThreadExecutor();
        var settings = systemSettings.getSettings();

        executor.execute(() -> {
            var count = 0;
            try {
                while (true) {
                    TimeUnit.MILLISECONDS.sleep(500);
                    var result = solutionDao.getSolutionByUidAndTime(uid, time, settings.isShowPassedPoints());

                    if (result == null) {
                        continue;
                    }

                    var event = SseEmitter.event()
                            .data(objectMapper.writeValueAsString(result))
                            .name("message");

                    emitter.send(event);
                    count += 1;

                    if (result.getState() == 0) {
                        emitter.complete();
                        break;
                    }

                    if (count == MAX_COUNT) {
                        log.warn("轮询超时");
                        emitter.complete();
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                // complete 会触发客户端 EventSource 的 onerror 方法
                emitter.complete();
            }
        });

        return emitter;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<Solution> getSolution(Integer uid, Integer sid) {
        var settings = systemSettings.getSettings();
        var data = solutionDao.getSolution(uid, sid, settings.isShowPassedPoints());

        data.ifPresent((s) -> s.setSourceCode(solutionDao.getSourceCode(sid)));

        return data;
    }
}

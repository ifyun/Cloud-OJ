package cloud.oj.core.service;

import cloud.oj.core.dao.SolutionDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SolutionService {

    private final SolutionDao solutionDao;

    private final SystemSettings settings;

    private final ObjectMapper objectMapper;

    public SolutionService(ObjectMapper objectMapper, SolutionDao solutionDao, SystemSettings settings) {
        this.objectMapper = objectMapper;
        this.solutionDao = solutionDao;
        this.settings = settings;
    }

    public List<List<?>> getSolutions(Integer uid, Integer page, Integer limit, Integer filter, String filterValue) {
        return solutionDao.getSolutionsByUser(uid, (page - 1) * limit, limit, filter, filterValue);
    }

    /**
     * 根据 id 获取提交
     * <p>隔离级别：读未提交</p>
     *
     * @return {@link SseEmitter}
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public SseEmitter getBySolutionId(String solutionId) {
        var emitter = new SseEmitter(0L);
        var executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                while (true) {
                    var result = solutionDao.getSolutionById(solutionId, settings.getSettings().isShowPassedPoints());

                    if (result == null) {
                        TimeUnit.MILLISECONDS.sleep(500);
                        continue;
                    }

                    var event = SseEmitter.event()
                            .data(objectMapper.writeValueAsString(result))
                            .name("message");

                    emitter.send(event);

                    if (result.getState() == 0) {
                        break;
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}

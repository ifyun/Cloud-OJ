package cloud.oj.core.service;

import cloud.oj.core.dao.SolutionDao;
import cloud.oj.core.entity.JudgeResult;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class SolutionService {

    private final SolutionDao solutionDao;

    private final SystemSettings settings;

    @Autowired
    public SolutionService(SolutionDao solutionDao, SystemSettings settings) {
        this.solutionDao = solutionDao;
        this.settings = settings;
    }

    public List<List<?>> getSolutions(String userId, Integer page, Integer limit, Integer filter, String filterValue) {
        return solutionDao.getSolutionsByUser(userId, (page - 1) * limit, limit, filter, filterValue);
    }

    @SneakyThrows
    public Optional<JudgeResult> getBySolutionId(String solutionId) {
        var count = 15;
        JudgeResult result;

        while (count > 0) {
            result = solutionDao.getSolutionById(solutionId, settings.getSettings().isShowPassedPoints());

            if (result != null) {
                return Optional.of(result);
            }

            TimeUnit.SECONDS.sleep(1);
            count--;
        }

        return Optional.empty();
    }
}

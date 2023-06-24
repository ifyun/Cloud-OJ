package cloud.oj.core.service;

import cloud.oj.core.dao.SolutionDao;
import cloud.oj.core.entity.Solution;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 根据 id 获取提交
     * <p>隔离级别：读未提交</p>
     *
     * @return {@link Optional} of {@link Solution}
     */
    @SneakyThrows
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Optional<Solution> getBySolutionId(String solutionId) {
        var count = 15;
        Solution result;

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

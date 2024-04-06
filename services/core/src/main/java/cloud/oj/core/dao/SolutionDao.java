package cloud.oj.core.dao;

import cloud.oj.core.entity.Solution;

import java.util.List;
import java.util.Optional;

public interface SolutionDao {

    Integer getResultOfContest(Integer uid, Integer cid, Integer pid);

    Integer getResult(Integer uid, Integer pid);

    /**
     * 隔离级别：读未提交
     */
    Solution getSolutionByUidAndTime(Integer uid, Long time, boolean showPassed);

    Optional<Solution> getSolution(Integer uid, Integer sid, boolean showPassed);

    String getSourceCode(Integer sid);

    List<List<?>> getSolutionsByUser(Integer uid, int start, int limit, Integer filter, String filterValue);

    void updateTitle(String title, Integer pid);

    void deleteByUser(Integer uid);
}

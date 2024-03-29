package cloud.oj.core.dao;

import cloud.oj.core.entity.Solution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SolutionDao {

    @Select("""
            select min(result - 1)
            from solution
            where uid = #{uid}
              and contest_id = #{cid}
              and problem_id = #{pid}
            """)
    Integer getResultOfContest(Integer uid, Integer cid, Integer pid);

    @Select("""
            select min(result - 1)
            from solution
            where uid = #{uid}
              and problem_id = #{pid}
            """)
    Integer getResult(Integer uid, Integer pid);

    /**
     * 隔离级别：读未提交
     */
    Solution getSolutionByUidAndTime(Integer uid, Long time, boolean showPassed);

    Optional<Solution> getSolution(Integer uid, Integer sid, boolean showPassed);

    @Select("""
            select code
            from source_code
            where solution_id = #{sid}
            """)
    String getSourceCode(Integer sid);

    List<List<?>> getSolutionsByUser(Integer uid, int start, int limit, Integer filter, String filterValue);

    void updateTitle(String title, Integer pid);

    void deleteByUser(Integer uid);
}

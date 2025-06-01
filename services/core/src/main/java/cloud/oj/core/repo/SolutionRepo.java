package cloud.oj.core.repo;

import cloud.oj.core.entity.Solution;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SolutionRepo {

    private final JdbcClient client;

    public Optional<Integer> selectResult(Integer uid, Integer pid) {
        return client.sql("""
                        select min(result - 1)
                        from solution
                        where uid = :uid
                          and problem_id = :pid
                        """)
                .param("uid", uid)
                .param("pid", pid)
                .query(Integer.class)
                .optional();
    }

    public Optional<Integer> selectResultOfContest(Integer uid, Integer cid, Integer pid) {
        return client.sql("""
                        select min(result - 1)
                        from solution
                        where uid = :uid
                          and contest_id = :cid
                          and problem_id = :pid
                        """)
                .param("uid", uid)
                .param("cid", cid)
                .param("pid", pid)
                .query(Integer.class)
                .optional();
    }

    /**
     * 根据 uid 和提交时间查询结果
     *
     * @param uid        用户 Id
     * @param time       UNIX 时间戳(13)
     * @param showPassed 是否查询通过的测试点
     * @return {@link Solution}
     */
    public Optional<Solution> selectByUidAndTime(Integer uid, Long time, Boolean showPassed) {
        return client.sql("""
                        select state - 1                                      as state,
                               result - 1                                     as result,
                               truncate(pass_rate, 2)                         as pass_rate,
                               truncate(score, 2)                             as score,
                               case when :showPassed = true then total end    as total,
                               case when :showPassed = true then passed end   as passed,
                               time,
                               memory,
                               error_info
                        from solution
                        where uid = :uid
                          and submit_time = :time
                        """)
                .param("uid", uid)
                .param("time", time)
                .param("showPassed", showPassed)
                .query(Solution.class)
                .optional();
    }

    /**
     * 查询题解
     *
     * @param uid        用户 Id
     * @param sid        题解 Id
     * @param showPassed 是否查询通过的测试点
     * @return {@link Solution}
     */
    public Optional<Solution> selectByUidAndSid(Integer uid, String sid, Boolean showPassed) {
        return client.sql("""
                        select solution_id,
                               problem_id,
                               title,
                               language,
                               state - 1                                      as state,
                               result - 1                                     as result,
                               truncate(pass_rate, 2)                         as pass_rate,
                               truncate(score, 2)                             as score,
                               case when :showPassed = true then total end    as total,
                               case when :showPassed = true then passed end   as passed,
                               time,
                               memory,
                               submit_time,
                               error_info
                        from solution
                        where solution_id = :sid
                          and uid = :uid
                          and deleted = 0
                        """)
                .param("uid", uid)
                .param("sid", sid)
                .param("showPassed", showPassed)
                .query(Solution.class)
                .optional();
    }

    /**
     * (Admin) 查询题解
     *
     * @param sid 题解 Id
     * @return {@link Solution}
     */
    public Optional<Solution> selectBySid(String sid) {
        return client.sql("""
                        select solution_id,
                               problem_id,
                               uid,
                               title,
                               language,
                               state - 1                                      as state,
                               result - 1                                     as result,
                               truncate(pass_rate, 2)                         as pass_rate,
                               truncate(score, 2)                             as score,
                               total,
                               passed,
                               time,
                               memory,
                               submit_time,
                               error_info
                        from solution
                        where solution_id = :sid
                          and deleted = 0
                        """)
                .param("sid", sid)
                .query(Solution.class)
                .optional();
    }

    public Optional<String> selectSourceCode(String sid) {
        return client.sql("""
                        select code
                        from source_code
                        where solution_id = :sid
                        """)
                .param("sid", sid)
                .query(String.class)
                .optional();
    }

    public List<Solution> selectAllByUid(Integer uid, Integer page, Integer size, Integer filter, String value) {
        return client.sql("""
                        select sql_calc_found_rows solution_id,
                                                   problem_id,
                                                   title,
                                                   language,
                                                   state - 1              as state,
                                                   result - 1             as result,
                                                   truncate(pass_rate, 2) as pass_rate,
                                                   truncate(score, 2)     as score,
                                                   time,
                                                   memory,
                                                   submit_time
                        from solution
                        where deleted = 0
                          and uid = :uid
                          and if(:filter = 1, problem_id = :value, true)
                          and if(:filter = 2, title like concat('%', :value, '%'), true)
                        order by submit_time desc
                        limit :start, :count
                        """)
                .param("uid", uid)
                .param("start", (page - 1) * size)
                .param("count", size)
                .param("filter", filter)
                .param("value", value)
                .query(Solution.class)
                .list();
    }

    /**
     * 根据过滤条件查询题解
     *
     * @param uid  用户 Id
     * @param pid  题目 Id
     * @param date 日期 Timestamp
     */
    public List<Solution> selectAllByFilter(Integer uid, Integer pid, Long date, Integer page, Integer size) {
        return client.sql("""
                        select sql_calc_found_rows solution_id,
                                                   problem_id,
                                                   title,
                                                   language,
                                                   state - 1              as state,
                                                   result - 1             as result,
                                                   truncate(pass_rate, 2) as pass_rate,
                                                   truncate(score, 2)     as score,
                                                   time,
                                                   memory,
                                                   submit_time
                        from solution
                        where deleted = 0
                          and if(not isnull(:pid), problem_id = :pid, true)
                          and if(not isnull(:uid), uid = :uid, true)
                          and if(not isnull(:date), submit_time - :date between 0 and 86400000, true)
                        order by submit_time desc
                        limit :start, :count
                        """)
                .param("uid", uid)
                .param("pid", pid)
                .param("date", date)
                .param("start", (page - 1) * size)
                .param("count", size)
                .query(Solution.class)
                .list();
    }

    public void updateTitle(Integer pid, String title) {
        client.sql("update solution set title = :title where problem_id = :pid")
                .param("pid", pid)
                .param("title", title)
                .update();
    }

    public Integer deleteByUid(Integer uid) {
        return client.sql("update solution set deleted = true where uid = :uid")
                .param("uid", uid)
                .update();
    }
}

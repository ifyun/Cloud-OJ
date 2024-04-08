package cloud.oj.core.repo;

import cloud.oj.core.entity.Solution;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SolutionRepo {

    private final DatabaseClient client;

    public Mono<Integer> selectResult(Integer uid, Integer pid) {
        return client.sql("""
                        select min(result - 1)
                        from solution
                        where uid = :uid
                          and problem_id = :pid
                        """)
                .bind("uid", uid)
                .bind("pid", pid)
                .mapValue(Integer.class)
                .first();
    }

    public Mono<Integer> selectResultOfContest(Integer uid, Integer cid, Integer pid) {
        return client.sql("""
                        select min(result - 1)
                        from solution
                        where uid = :uid
                          and contest_id = :cid
                          and problem_id = :pid
                        """)
                .bind("uid", uid)
                .bind("cid", cid)
                .bind("pid", pid)
                .mapValue(Integer.class)
                .first();
    }

    /**
     * 根据 uid 和提交时间查询结果
     *
     * @param uid        用户 Id
     * @param time       UNIX 时间戳(13)
     * @param showPassed 是否查询通过的测试点
     * @return {@link Solution}
     */
    public Mono<Solution> selectByUidAndTime(Integer uid, Long time, Boolean showPassed) {
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
                .bind("uid", uid)
                .bind("time", time)
                .bind("showPassed", showPassed)
                .mapProperties(Solution.class)
                .first();
    }

    /**
     * 查询题解
     *
     * @param uid        用户 Id
     * @param sid        题解 Id
     * @param showPassed 是否查询通过的测试点
     * @return {@link Solution}
     */
    public Mono<Solution> selectByUidAndSid(Integer uid, Integer sid, Boolean showPassed) {
        return client.sql("""
                        select problem_id,
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
                .bind("uid", uid)
                .bind("sid", sid)
                .bind("showPassed", showPassed)
                .mapProperties(Solution.class)
                .first();

    }

    public Mono<String> selectSourceCode(Integer sid) {
        return client.sql("""
                        select code
                        from source_code
                        where solution_id = :sid
                        """)
                .bind("sid", sid)
                .mapValue(String.class)
                .first();
    }

    public Flux<Solution> selectAllByUid(Integer uid, Integer page, Integer size, Integer filter, String value) {
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
                .bind("uid", uid)
                .bind("start", (page - 1) * size)
                .bind("count", size)
                .bind("filter", filter)
                .bind("value", value)
                .mapValue(Solution.class)
                .all();
    }

    public Mono<Long> updateTitle(Integer pid, String title) {
        return client.sql("update solution set title = :title where problem_id = :pid")
                .bind("pid", pid)
                .bind("title", title)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> deleteByUid(Integer uid) {
        return client.sql("update solution set deleted = true where uid = :uid")
                .bind("uid", uid)
                .fetch()
                .rowsUpdated();
    }
}

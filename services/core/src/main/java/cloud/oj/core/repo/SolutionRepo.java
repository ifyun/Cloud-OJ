package cloud.oj.core.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
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

    public Mono<Long> deleteByUser(Integer uid) {
        return client.sql("update solution set deleted = true where uid = :uid")
                .bind("uid", uid)
                .fetch()
                .rowsUpdated();
    }
}

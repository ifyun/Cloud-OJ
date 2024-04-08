package cloud.oj.core.repo;

import cloud.oj.core.entity.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ProblemRepo {

    private final DatabaseClient client;

    public Mono<Problem> selectById(Integer pid, Boolean onlyEnable) {
        return client.sql("""
                        select problem_id,
                               title,
                               description,
                               timeout,
                               memory_limit,
                               output_limit,
                               score,
                               category
                        from problem
                        where if(:onlyEnable = true, enable = true, true)
                          and problem_id = :pid
                          and deleted = false
                        """)
                .bind("onlyEnable", onlyEnable)
                .bind("pid", pid)
                .mapProperties(Problem.class)
                .first();
    }
}

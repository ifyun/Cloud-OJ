package cloud.oj.core.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SolutionRepo {

    private final DatabaseClient client;

    public Mono<Long> deleteByUser(Integer uid) {
        return client.sql("update solution set deleted = true where uid = :uid")
                .bind("uid", uid)
                .fetch()
                .rowsUpdated();
    }
}

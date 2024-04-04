package cloud.oj.core.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ScoreboardRepo {

    private final DatabaseClient client;

    /**
     * 逻辑删除用户的排名信息
     */
    public Mono<Long> deleteByUser(Integer uid) {
        return client.sql("update scoreboard set deleted = true where uid = :uid")
                .bind("uid", uid)
                .fetch()
                .rowsUpdated();
    }
}

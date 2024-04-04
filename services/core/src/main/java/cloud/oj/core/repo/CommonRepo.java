package cloud.oj.core.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CommonRepo {

    private final DatabaseClient client;

    /**
     * 用于分页查询
     */
    public Mono<Integer> selectFoundRows() {
        return client.sql("select found_rows()")
                .mapValue(Integer.class)
                .first();
    }

    /**
     * 设置当前会话的时区
     *
     * @param timezone 时区偏移，eg: +8:00
     */
    public Mono<Void> setTimezone(String timezone) {
        return client.sql("set time_zone = :timezone")
                .bind("timezone", timezone)
                .then();
    }
}

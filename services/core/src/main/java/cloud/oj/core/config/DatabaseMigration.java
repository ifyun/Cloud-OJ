package cloud.oj.core.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
@RequiredArgsConstructor
public class DatabaseMigration {

    private final JdbcClient client;

    @PostConstruct
    public void init() {
        var sql = """
                create table if not exists data_conf
                (
                    problem_id int primary key      not null,
                    conf       json                 null
                )
                """;
        client.sql(sql).update();
    }
}

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
        var sql1 = "alter table user add column if not exists real_name char(16) null after nickname";
        var sql2 = "create index if not exists idx_real_name on user (real_name)";

        client.sql(sql1).update();
        client.sql(sql2).update();
    }
}

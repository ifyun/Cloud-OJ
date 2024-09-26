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
        var sql1 = """
                create table if not exists data_conf
                (
                    problem_id int primary key      not null,
                    conf       json                 null
                )
                """;
        var sql2 = """
                create table if not exists log
                (
                    id          bigint unsigned primary key auto_increment,
                    service     char(16),
                    instance_id char(36),
                    level       char(8)  not null,
                    thread      char(64) not null,
                    class_name  tinytext not null,
                    message     text,
                    time        bigint   not null
                ) engine = Aria
                """;
        var sql3 = "create index if not exists idx_level on log(level)";
        var sql4 = "create index if not exists idx_time on log (time)";

        client.sql(sql1).update();
        client.sql(sql2).update();
        client.sql(sql3).update();
        client.sql(sql4).update();
    }
}

package cloud.oj.core.repo;

import cloud.oj.core.entity.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SettingsRepo {

    private final DatabaseClient client;

    public Mono<Settings> select() {
        return client.sql("select * from settings where id = 0 for update")
                .mapProperties(Settings.class)
                .first();
    }

    public Mono<Long> update(Settings settings) {
        return client.sql("""
                        update settings
                        set always_show_ranking = #{alwaysShowRanking},
                            show_all_contest    = #{showAllContest},
                            show_passed_points  = #{showPassedPoints},
                            auto_del_solutions  = #{autoDelSolutions}
                        where id = 0
                        """)
                .bindProperties(settings)
                .fetch()
                .rowsUpdated();
    }
}

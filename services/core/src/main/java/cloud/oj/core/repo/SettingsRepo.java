package cloud.oj.core.repo;

import cloud.oj.core.entity.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SettingsRepo {

    private final JdbcClient client;

    public Settings select() {
        return client.sql("select * from settings where id = 0 for update")
                .query(Settings.class)
                .single();
    }

    public Integer update(Settings settings) {
        return client.sql("""
                        update settings
                        set always_show_ranking = :alwaysShowRanking,
                            show_all_contest    = :showAllContest,
                            show_passed_points  = :showPassedPoints,
                            auto_del_solutions  = :autoDelSolutions
                        where id = 0
                        """)
                .paramSource(settings)
                .update();
    }
}

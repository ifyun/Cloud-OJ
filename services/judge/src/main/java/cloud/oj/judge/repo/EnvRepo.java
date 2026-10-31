package cloud.oj.judge.repo;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cloud.oj.judge.entity.Env;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EnvRepo {

    private final JdbcClient client;

    @Transactional
    public void update(List<Env> envList) {
        for (var e : envList) {
            client.sql("""
                    INSERT INTO env(id, lang, ver)
                    VALUES
                        (:id, :lang, :ver)
                    ON DUPLICATE KEY UPDATE
                        lang = VALUES(lang),
                        ver = VALUES(ver)
                    """)
                    .paramSource(e)
                    .update();
        }
    }
}

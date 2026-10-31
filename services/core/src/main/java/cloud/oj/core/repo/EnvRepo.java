package cloud.oj.core.repo;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EnvRepo {

    private final JdbcClient client;

    public List<Map<String, Object>> get() {
        return client.sql("SELECT * FROM env").query().listOfRows();
    }
}

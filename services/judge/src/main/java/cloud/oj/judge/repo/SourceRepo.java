package cloud.oj.judge.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SourceRepo {

    private final JdbcClient client;

    /**
     * 写入题解代码
     *
     * @param sid    题解 Id
     * @param source 源代码
     */
    public void insert(String sid, String source) {
        client.sql("insert into source_code(solution_id, code) values(:sid, :source)")
                .param("sid", sid)
                .param("source", source)
                .update();
    }
}

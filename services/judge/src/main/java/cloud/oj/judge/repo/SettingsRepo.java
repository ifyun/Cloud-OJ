package cloud.oj.judge.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SettingsRepo {

    private final JdbcClient client;

    /**
     * 查询是否自动删除题解临时文件
     *
     * @return {@link Boolean}
     */
    public Boolean autoDelSolutions() {
        return client.sql("select auto_del_solutions from settings where id = 0")
                .query(Boolean.class)
                .single();
    }
}

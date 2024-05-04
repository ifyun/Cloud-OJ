package cloud.oj.core.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommonRepo {

    private final JdbcClient client;

    /**
     * 用于分页查询
     */
    public Integer selectFoundRows() {
        return client.sql("select found_rows()")
                .query(Integer.class)
                .single();
    }

    /**
     * 设置当前会话的时区
     *
     * @param timezone 时区偏移，eg: +8:00
     */
    public void setTimezone(String timezone) {
        client.sql("set time_zone = :timezone")
                .param("timezone", timezone)
                .query();
    }
}

package cloud.oj.core.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommonRepo {

    private final JdbcClient client;

    /**
     * 设置当前会话的时区
     *
     * @param timezone 时区，eg: +8:00, Asia/Shanghai
     */
    public void setTimezone(String timezone) {
        client.sql("set time_zone = :timezone")
                .param("timezone", timezone)
                .update();
    }
}

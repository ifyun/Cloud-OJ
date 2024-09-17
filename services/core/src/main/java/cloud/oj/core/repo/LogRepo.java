package cloud.oj.core.repo;

import cloud.oj.core.entity.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LogRepo {

    private final JdbcClient client;

    /**
     * 查询最近的 10 条日志
     *
     * @param time 起始时间
     * @return {@link List} of {@link Log}
     */
    public List<Log> selectLatest10(long time) {
        return client.sql("""
                        select *
                        from (select * from log where time > :time order by time desc limit 10) t
                        order by time
                        """)
                .param("time", time)
                .query(Log.class)
                .list();
    }

    /**
     * 按时间范围查询日志
     *
     * @param start 其实时间
     * @param end   结束时间
     * @return {@link List} of {@link Log}
     */
    public List<Log> selectRange(long start, long end) {
        return client.sql("""
                        select * from log where time >= :start and time <= :end
                        """)
                .param("start", start)
                .param("end", end)
                .query(Log.class)
                .list();
    }
}

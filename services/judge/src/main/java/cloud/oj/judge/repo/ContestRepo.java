package cloud.oj.judge.repo;

import cloud.oj.judge.entity.Contest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContestRepo {

    private final JdbcClient client;

    public Optional<Contest> selectById(Integer cid) {
        return client.sql("""
                        select contest_name,
                               if(start_at <= unix_timestamp(now()), true, false) as started,
                               if(end_at <= unix_timestamp(now()), true, false)   as ended,
                               languages
                        from contest
                        where contest_id = :cid;
                        """)
                .param("cid", cid)
                .query(Contest.class)
                .optional();
    }
}

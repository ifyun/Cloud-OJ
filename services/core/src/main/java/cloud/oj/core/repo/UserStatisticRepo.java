package cloud.oj.core.repo;

import cloud.oj.core.entity.HeatmapData;
import cloud.oj.core.entity.Language;
import cloud.oj.core.entity.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserStatisticRepo {

    private final JdbcClient client;

    public List<Language> selectLanguages(Integer uid) {
        return client.sql("""
                        select language, count(solution_id) as count
                        from solution
                        where contest_id is null
                          and uid = :uid
                        group by language
                        """)
                .param("uid", uid)
                .query(Language.class)
                .list();
    }

    public List<HeatmapData> selectHeatmap(Integer uid, Long start, Long end) {
        return client.sql("""
                        -- 一年内同一题目同一语言仅算一次
                        with yearly_ac as (
                            select problem_id,
                                   language,
                                   min(submit_time)                        as timestamp,
                                   year(from_unixtime(submit_time / 1000)) as ac_year
                            from solution
                            where contest_id is null
                              and uid = :uid
                              and result = 'AC'
                              and submit_time >= :start
                              and submit_time < :end
                            group by problem_id, language, ac_year
                        )
                        select unix_timestamp(date(from_unixtime(timestamp / 1000))) * 1000 as timestamp,
                               count(*)                                                     as value
                        from yearly_ac
                        group by date(from_unixtime(timestamp / 1000))
                        order by timestamp;
                        """)
                .param("uid", uid)
                .param("start", start, Types.BIGINT)
                .param("end", end, Types.BIGINT)
                .query(HeatmapData.class)
                .list();
    }

    public Results selectResults(Integer uid) {
        return client.sql("""
                        select count(result = 'AC' or null)  as AC,
                               count(result = 'WA' or null)  as WA,
                               count(result = 'TLE' or null) as TLE,
                               count(result = 'MLE' or null) as MLE,
                               count(result = 'RE' or null)  as RE,
                               count(result = 'CE' or null)  as CE,
                               count(solution_id)            as total
                        from solution
                        where uid = :uid
                          and contest_id is null
                          and result <> 'JUDGE_ERROR'
                        """)
                .param("uid", uid)
                .query(Results.class)
                .single();
    }
}

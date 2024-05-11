package cloud.oj.core.repo;

import cloud.oj.core.entity.Language;
import cloud.oj.core.entity.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Integer> selectActivities(Integer uid, Integer year) {
        return client.sql("""
                        select DATE_FORMAT(from_unixtime(submit_time / 1000), '%Y-%m-%d') as date,
                               count(distinct problem_id, language)                       as count
                        from solution
                        where contest_id is null
                          and uid = :uid
                          and year(from_unixtime(submit_time / 1000)) = :year
                          and result = 'AC'
                        group by date;
                        """)
                .param("uid", uid)
                .param("year", year)
                .query(rs -> {
                    var map = new HashMap<String, Integer>();
                    while (rs.next()) {
                        map.put(rs.getString("date"), rs.getInt("count"));
                    }
                    return map;
                });
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

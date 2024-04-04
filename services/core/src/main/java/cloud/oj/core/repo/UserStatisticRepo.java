package cloud.oj.core.repo;

import cloud.oj.core.entity.Activity;
import cloud.oj.core.entity.Language;
import cloud.oj.core.entity.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserStatisticRepo {

    private final DatabaseClient client;

    public Flux<Language> selectLanguages(Integer uid) {
        return client.sql("select language, count(solution_id) as count" +
                        "    from solution" +
                        "    where contest_id is null" +
                        "      and uid = :uid" +
                        "    group by language")
                .bind("uid", uid)
                .mapProperties(Language.class)
                .all();
    }

    public Flux<Activity> selectActivities(Integer uid, Integer year) {
        return client.sql("select DATE_FORMAT(from_unixtime(submit_time / 1000), '%Y-%m-%d') as date," +
                        "         count(problem_id)                                          as count" +
                        "  from (select problem_id, submit_time" +
                        "        from solution" +
                        "        where contest_id is null" +
                        "          and uid = :uid" +
                        "          and year(from_unixtime(submit_time / 1000)) = :year" +
                        "          and result = 'AC'" +
                        "        group by problem_id, submit_time) s" +
                        "  group by date")
                .bind("uid", uid)
                .bind("year", year)
                .mapProperties(Activity.class)
                .all();
    }

    public Mono<Result> selectResults(Integer uid) {
        return client.sql("select count(result = 'AC' or null)  as AC," +
                        "         count(result = 'WA' or null)  as WA," +
                        "         count(result = 'TLE' or null) as TLE," +
                        "         count(result = 'MLE' or null) as MLE," +
                        "         count(result = 'RE' or null)  as RE," +
                        "         count(result = 'CE' or null)  as CE," +
                        "         count(solution_id)            as total" +
                        "  from solution" +
                        "  where uid = :uid" +
                        "    and contest_id is null" +
                        "    and result <> 'JUDGE_ERROR'")
                .bind("uid", uid)
                .mapProperties(Result.class)
                .first();
    }
}

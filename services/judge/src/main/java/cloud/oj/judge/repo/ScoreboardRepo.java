package cloud.oj.judge.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScoreboardRepo {

    private final JdbcClient client;

    /**
     * 提交次数 +1
     *
     * @param uid  用户 Id
     * @param time 更新时间(unix timestamp)
     */
    public void incSubmitted(Integer uid, Long time) {
        var sql = """
                insert into scoreboard
                set uid   = :uid,
                    committed = committed + 1,
                    update_time = :time
                on duplicate key update committed = committed + 1
                """;
        client.sql(sql)
                .param("uid", uid)
                .param("time", time)
                .update();
    }

    /**
     * 提交次数 +1（竞赛）
     *
     * @param uid  用户 Id
     * @param cid  竞赛 Id
     * @param time 更新时间(unix timestamp)
     */
    public void incSubmittedOfContest(Integer uid, Integer cid, Long time) {
        var sql = """
                insert into scoreboard_contest
                set uid         = :uid,
                    contest_id  = :cid,
                    committed   = committed + 1,
                    update_time = :time
                on duplicate key update committed = committed + 1
                """;
        client.sql(sql)
                .param("uid", uid)
                .param("cid", cid)
                .param("time", time)
                .update();
    }

    /**
     * 更新排名
     *
     * @param uid  用户 Id
     * @param time 更新时间(unix timestamp)
     */
    public void update(Integer uid, Long time) {
        var sql = """
                replace into scoreboard(uid, committed, passed, score, update_time)
                select :uid                                          as uid,
                       sum(committed)                                as committed,
                       count((case when (pass_rate = 1) then 1 end)) as passed,
                       sum(score)                                    as score,
                       :time                                         as update_time
                from (select count(problem_id) as committed,
                             max(pass_rate)    as pass_rate,
                             max(score)        as score
                      from solution
                      where contest_id is null
                        and uid = :uid
                      group by problem_id) as user_score;
                """;
        client.sql(sql)
                .param("uid", uid)
                .param("time", time)
                .update();
    }

    /**
     * 更新排名（竞赛）
     *
     * @param cid  竞赛 Id
     * @param uid  用户 Id
     * @param time 更新时间(unix timestamp)
     */
    public void updateOfContest(Integer cid, Integer uid, Long time) {
        var sql = """
                replace into scoreboard_contest(uid, contest_id, committed, passed, score, update_time)
                select :uid                                          as uid,
                       :cid                                          as contest_id,
                       sum(committed)                                as committed,
                       count((case when (pass_rate = 1) then 1 end)) as passed,
                       sum(score)                                    as score,
                       :time                                         as update_time
                from (select count(problem_id) as committed,
                             max(pass_rate)    as pass_rate,
                             max(score)        as score
                      from solution
                      where contest_id = :cid
                        and uid = :uid
                      group by problem_id) as user_score;
                """;
        client.sql(sql)
                .param("cid", cid)
                .param("uid", uid)
                .param("time", time)
                .update();
    }
}

package cloud.oj.core.repo;

import cloud.oj.core.entity.Ranking;
import cloud.oj.core.entity.ScoreDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScoreboardRepo {

    private final JdbcClient client;

    public List<Ranking> selectAll(Integer page, Integer size, boolean admin) {
        return client.sql("""
                        select sql_calc_found_rows (@ranking := @ranking + 1) as `rank`,
                                                   u.uid,
                                                   u.nickname,
                                                   if (:admin, u.username, null) as username,
                                                   if (:admin, u.real_name, null) as real_name,
                                                   u.has_avatar,
                                                   u.star,
                                                   committed,
                                                   passed,
                                                   truncate(score, 2)         as score
                        from scoreboard l
                                 join user u on l.deleted = false and l.uid = u.uid,
                             (select @ranking := :start) rn
                        where u.role = 1
                          and score > 0
                        order by score desc, update_time
                        limit :start, :count
                        """)
                .param("admin", admin)
                .param("start", (page - 1) * size)
                .param("count", size)
                .query(Ranking.class)
                .list();
    }

    public List<Ranking> selectByCid(Integer cid, boolean admin) {
        return client.sql("""
                        select (@ranking := @ranking + 1) as `rank`,
                               u.uid,
                               u.nickname,
                               if (:admin, u.username, null) as username,
                               if (:admin, u.real_name, null) as real_name,
                               u.has_avatar,
                               u.star,
                               committed,
                               passed,
                               truncate(score, 2)         as score
                        from scoreboard_contest lc
                                  join user u on lc.deleted = false and lc.uid = u.uid,
                             (select @ranking := 0) rn
                        where contest_id = :cid
                          and u.role = 1
                          and score > 0
                        order by score desc, update_time
                        """)
                .param("admin", admin)
                .param("cid", cid)
                .query(Ranking.class)
                .list();
    }

    public List<ScoreDetail> selectScores(Integer uid, Integer cid) {
        return client.sql("""
                        select *
                        from (select problem_id,
                                     result - 1                                                          as result,
                                     truncate(score, 2)                                                  as score,
                                     row_number() over (partition by problem_id order by pass_rate desc) as rn
                              from solution
                              where contest_id = :cid
                                and uid = :uid
                                and state = 1) t
                        where rn = 1
                        order by problem_id
                        """)
                .param("uid", uid)
                .param("cid", cid)
                .query(ScoreDetail.class)
                .list();
    }

    /**
     * 逻辑删除用户的排名信息
     */
    public Integer deleteByUid(Integer uid) {
        return client.sql("update scoreboard set deleted = true where uid = :uid")
                .param("uid", uid)
                .update();
    }
}

package cloud.oj.core.repo;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.entity.ProblemOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ContestRepo {

    private final DatabaseClient client;

    public Mono<String> selectInviteKey(Integer cid) {
        return client.sql("select invite_key from contest where contest_id = :cid")
                .bind("cid", cid)
                .mapValue(String.class)
                .first();
    }

    /**
     * 为竞赛生成新邀请码
     *
     * @return 新邀请码
     */
    public Mono<Long> newInviteKey(Integer cid, String key) {
        return client.sql("update contest set invite_key = :key where contest_id = :cid")
                .bind("key", key)
                .bind("cid", cid)
                .fetch()
                .rowsUpdated();
    }

    /**
     * 查询指定竞赛的题目顺序
     */
    public Flux<ProblemOrder> selectOrders(Integer cid) {
        return client.sql("""
                        select problem_id,
                               `order`
                        from `contest-problem`
                        where contest_id = :cid
                        order by problem_id
                        """)
                .bind("cid", cid)
                .mapProperties(ProblemOrder.class)
                .all();
    }

    /**
     * 更新竞赛题目的顺序
     *
     * @param cid   竞赛 Id
     * @param pid   题目 Id
     * @param order 顺序值，表示排第几个
     */
    public Mono<Long> updateOrder(Integer cid, Integer pid, Integer order) {
        return client.sql("""
                        update `contest-problem`
                        set `order` = :order
                        where contest_id = :cid
                          and problem_id = :pid
                        """)
                .bind("order", order)
                .bind("cid", cid)
                .bind("pid", pid)
                .fetch()
                .rowsUpdated();
    }

    /**
     * 查询指定竞赛的题目数量
     */
    public Mono<Integer> countProblems(Integer cid) {
        return client.sql("""
                        select count(problem_id)
                        from `contest-problem`
                        where contest_id = :cid
                        """)
                .bind("cid", cid)
                .mapValue(Integer.class)
                .first();
    }

    public Mono<Contest> selectById(Integer cid) {
        return client.sql("""
                        select *,
                               if(start_at <= unix_timestamp(now()), true, false) as started,
                               if(end_at <= unix_timestamp(now()), true, false)   as ended
                        from contest
                        where contest_id = :cid
                          and deleted = false
                        """)
                .bind("cid", cid)
                .mapProperties(Contest.class)
                .first();
    }

    /**
     * 查询所有已开始的竞赛
     */
    public Flux<Contest> selectAllStarted(Integer page, Integer size) {
        return client.sql("""
                        select sql_calc_found_rows c.contest_id as contest_id,
                                                   contest_name,
                                                   languages,
                                                   start_at,
                                                   end_at,
                                                   if(start_at <= unix_timestamp(), true, false) as started,
                                                   if(end_at <= unix_timestamp(), true, false)   as ended,
                                                   count(cp.problem_id)                          as problem_count
                        from contest c
                                 left join `contest-problem` cp on c.contest_id = cp.contest_id
                        where c.deleted = false
                          and start_at <= unix_timestamp(now())
                        group by c.contest_id
                        order by started desc, ended
                        limit :start, :count
                        """)
                .bind("start", (page - 1) * size)
                .bind("count", size)
                .mapProperties(Contest.class)
                .all();
    }

    public Flux<Contest> selectAll(Integer page, Integer size, Boolean isAdmin) {
        return client.sql("""
                                
                        select sql_calc_found_rows c.contest_id                                  as contest_id,
                                                   contest_name,
                                                   if(:admin, invite_key, null)                  as invite_key,
                                                   languages,
                                                   start_at,
                                                   end_at,
                                                   create_at,
                                                   if(start_at <= unix_timestamp(), true, false) as started,
                                                   if(end_at <= unix_timestamp(), true, false)   as ended,
                                                   count(cp.problem_id)                          as problem_count
                        from contest c
                                 left join `contest-problem` cp on c.contest_id = cp.contest_id
                        where c.deleted = false
                        group by c.contest_id
                        order by started desc, ended
                        limit :start, :count
                        """)
                .bind("admin", isAdmin)
                .bind("start", (page - 1) * size)
                .bind("count", size)
                .mapProperties(Contest.class)
                .all();
    }

    public Mono<Long> insert(Contest contest) {
        return client.sql("""
                        insert into contest(contest_name, invite_key, start_at, end_at, languages)
                        values (:contestName, :inviteKey, :startAt, :endAt, :languages)
                        """)
                .bindProperties(contest)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> update(Contest contest) {
        return client.sql("""
                        update contest
                        set contest_name = :contestName,
                            start_at     = :startAt,
                            end_at       = :endAt,
                            languages    = :languages
                        where contest_id = :contestId
                        """)
                .bindProperties(contest)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> delete(Integer cid) {
        return client.sql("""
                        update contest
                        set deleted = true
                        where contest_id = :cid
                        """)
                .bind("cid", cid)
                .fetch()
                .rowsUpdated();
    }

    /**
     * 查询所有可用题目
     */
    public Flux<Problem> selectIdleProblems(Integer cid, Integer page, Integer size) {
        return client.sql("""
                        select sql_calc_found_rows *
                        from problem
                        where deleted = false
                          and enable = false
                          and problem_id
                            not in
                              (select problem_id from contest_problem where contest_id = :cid)
                        limit :start, :count
                        """)
                .bind("cid", cid)
                .bind("start", (page - 1) * size)
                .bind("count", size)
                .mapProperties(Problem.class)
                .all();
    }

    /**
     * 查询指定竞赛中的所有题目
     * @param cid 竞赛 ID
     * @param onlyStarted 仅从已开始的竞赛中查询
     */
    public Flux<Problem> selectProblems(Integer cid, Boolean onlyStarted) {
        return client.sql("""
                        select contest_id,
                               contest_name,
                               start_at,
                               end_at,
                               problem_id,
                               title,
                               score
                        from contest_problem
                        where contest_id = :cid
                          and if(:onlyStarted = true, start_at <= unix_timestamp(now()), true)
                        order by `order`
                        """)
                .bind("cid", cid)
                .bind("onlyStarted", onlyStarted)
                .mapProperties(Problem.class)
                .all();
    }

    public Mono<Long> addProblem(Integer cid, Integer pid) {
        return client.sql("""
                        insert into `contest-problem`(contest_id, problem_id)
                        values (:cid, :pid)
                        """)
                .bind("cid", cid)
                .bind("pid", pid)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> removeProblem(Integer cid, Integer pid) {
        return client.sql("""
                        delete
                        from `contest-problem`
                        where contest_id = :cid
                          and problem_id = :pid
                        """)
                .bind("cid", cid)
                .bind("pid", pid)
                .fetch()
                .rowsUpdated();
    }
}

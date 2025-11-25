package cloud.oj.core.repo;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.ContestFilter;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.entity.ProblemOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContestRepo {

    private final JdbcClient client;

    public Optional<String> selectInviteKey(Integer cid) {
        return client.sql("select invite_key from contest where contest_id = :cid")
                .param("cid", cid)
                .query(String.class)
                .optional();
    }

    /**
     * 为竞赛生成新邀请码
     *
     * @return 新邀请码
     */
    public Integer newInviteKey(Integer cid, String key) {
        return client.sql("update contest set invite_key = :key where contest_id = :cid")
                .param("key", key)
                .param("cid", cid)
                .update();
    }

    /**
     * 查询指定竞赛的题目顺序
     */
    public List<ProblemOrder> selectOrders(Integer cid) {
        return client.sql("""
                        select problem_id,
                               `order`
                        from `contest-problem`
                        where contest_id = :cid
                        order by problem_id
                        """)
                .param("cid", cid)
                .query(ProblemOrder.class)
                .list();
    }

    /**
     * 更新竞赛题目的顺序
     *
     * @param cid   竞赛 Id
     * @param pid   题目 Id
     * @param order 顺序值，表示排第几个
     */
    public Integer updateOrder(Integer cid, Integer pid, Integer order) {
        return client.sql("""
                        update `contest-problem`
                        set `order` = :order
                        where contest_id = :cid
                          and problem_id = :pid
                        """)
                .param("order", order)
                .param("cid", cid)
                .param("pid", pid)
                .update();
    }

    /**
     * 查询指定竞赛的题目数量
     */
    public Integer countProblems(Integer cid) {
        return client.sql("""
                        select count(problem_id)
                        from `contest-problem`
                        where contest_id = :cid
                        """)
                .param("cid", cid)
                .query(Integer.class)
                .single();
    }

    /**
     * 查询竞赛题目
     */
    public Optional<Problem> selectProblem(Integer cid, Integer pid) {
        return client.sql("""
                        select problem_id,
                               title,
                               description,
                               timeout,
                               memory_limit,
                               output_limit,
                               score,
                               category,
                               languages
                        from contest_problem
                        where contest_id = :cid
                          and problem_id = :pid
                          and start_at <= now()
                        """)
                .param("cid", cid)
                .param("pid", pid)
                .query(Problem.class)
                .optional();
    }

    public Optional<Contest> selectById(Integer cid) {
        return client.sql("""
                        select contest_id,
                               contest_name,
                               start_at,
                               end_at,
                               if(start_at <= unix_timestamp(now()), true, false) as started,
                               if(end_at <= unix_timestamp(now()), true, false)   as ended,
                               languages,
                               create_at
                        from contest
                        where contest_id = :cid
                          and deleted = false
                        """)
                .param("cid", cid)
                .query(Contest.class)
                .optional();
    }

    /**
     * 分页查询已开始的竞赛
     */
    public List<Contest> selectAllStarted(ContestFilter filter, Integer page, Integer size) {
        return client.sql("""
                        select count(*) over ()                              as _total,
                               contest_id,
                               contest_name,
                               languages,
                               start_at,
                               end_at,
                               if(start_at <= unix_timestamp(), true, false) as started,
                               if(end_at <= unix_timestamp(), true, false)   as ended
                        from contest
                        where deleted = false
                          and start_at <= unix_timestamp(now())
                          and if(:hideEnded, end_at > unix_timestamp(now()), true)
                          and if(LENGTH(:keyword) > 0, contest_name like concat('%', :keyword, '%'), true)
                        order by start_at desc, end_at
                        limit :start, :count
                        """)
                .param("start", (page - 1) * size)
                .param("count", size)
                .param("hideEnded", filter.hideEnded)
                .param("keyword", filter.keyword)
                .query(Contest.class)
                .list();
    }

    /**
     * 分页查询所有竞赛
     */
    public List<Contest> selectAll(ContestFilter filter, Integer page, Integer size, Boolean isAdmin) {
        return client.sql("""
                        select count(*) over ()                              as _total,
                               contest_id,
                               contest_name,
                               if(:admin, invite_key, null)                  as invite_key,
                               languages,
                               start_at,
                               end_at,
                               create_at,
                               if(start_at <= unix_timestamp(), true, false) as started,
                               if(end_at <= unix_timestamp(), true, false)   as ended
                        from contest
                        where deleted = false
                          and if(:hideEnded, end_at > unix_timestamp(now()), true)
                          and if(LENGTH(:keyword) > 0, contest_name like concat('%', :keyword, '%'), true)
                        order by start_at desc, end_at
                        limit :start, :count
                        """)
                .param("admin", isAdmin)
                .param("start", (page - 1) * size)
                .param("count", size)
                .param("hideEnded", filter.hideEnded)
                .param("keyword", filter.keyword)
                .query(Contest.class)
                .list();
    }

    public Integer insert(Contest contest) {
        return client.sql("""
                        insert into contest(contest_name, invite_key, start_at, end_at, languages)
                        values (:contestName, :inviteKey, :startAt, :endAt, :languages)
                        """)
                .paramSource(contest)
                .update();
    }

    public Integer update(Contest contest) {
        return client.sql("""
                        update contest
                        set contest_name = :contestName,
                            start_at     = :startAt,
                            end_at       = :endAt,
                            languages    = :languages
                        where contest_id = :contestId
                        """)
                .paramSource(contest)
                .update();
    }

    public Integer delete(Integer cid) {
        return client.sql("""
                        update contest
                        set deleted = true
                        where contest_id = :cid
                        """)
                .param("cid", cid)
                .update();
    }

    /**
     * 分页查询可用题目
     */
    public List<Problem> selectIdleProblems(Integer cid, Integer page, Integer size) {
        return client.sql("""
                        select count(*) over () as _total,
                               p.problem_id,
                               p.title,
                               p.score
                        from problem p
                        where deleted = false
                          and enable = false
                          and problem_id
                            not in
                              (select problem_id from contest_problem where contest_id = :cid)
                        order by problem_id
                        limit :start, :count
                        """)
                .param("cid", cid)
                .param("start", (page - 1) * size)
                .param("count", size)
                .query(Problem.class)
                .list();
    }

    /**
     * 查询指定竞赛中的所有题目
     *
     * @param cid         竞赛 ID
     * @param onlyStarted 仅从已开始的竞赛中查询
     */
    public List<Problem> selectProblems(Integer cid, Boolean onlyStarted) {
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
                .param("cid", cid)
                .param("onlyStarted", onlyStarted)
                .query(Problem.class)
                .list();
    }

    public Integer addProblem(Integer cid, Integer pid) {
        return client.sql("""
                        insert into `contest-problem`(contest_id, problem_id)
                        values (:cid, :pid)
                        """)
                .param("cid", cid)
                .param("pid", pid)
                .update();
    }

    public Integer removeProblem(Integer cid, Integer pid) {
        return client.sql("""
                        delete
                        from `contest-problem`
                        where contest_id = :cid
                          and problem_id = :pid
                        """)
                .param("cid", cid)
                .param("pid", pid)
                .update();
    }
}

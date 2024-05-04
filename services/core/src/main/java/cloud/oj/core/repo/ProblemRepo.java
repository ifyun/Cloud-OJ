package cloud.oj.core.repo;

import cloud.oj.core.entity.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProblemRepo {

    private final JdbcClient client;

    public Optional<Problem> selectById(Integer pid, Boolean onlyEnable) {
        return client.sql("""
                        select problem_id,
                               title,
                               description,
                               timeout,
                               memory_limit,
                               output_limit,
                               score,
                               category
                        from problem
                        where if(:onlyEnable = true, enable = true, true)
                          and problem_id = :pid
                          and deleted = false
                        """)
                .param("onlyEnable", onlyEnable)
                .param("pid", pid)
                .query(Problem.class)
                .optional();
    }

    public List<Problem> selectAll(Integer page, Integer size, String keyword) {
        return client.sql("""
                        select sql_calc_found_rows problem_id,
                                                   title,
                                                   enable,
                                                   category as category,
                                                   score,
                                                   create_at
                        from problem
                        where deleted = false
                          and if(:keyword is not null,
                                 title like concat('%', :keyword, '%') or find_in_set(:keyword, category),
                                 true)
                        limit :start, :count
                        """)
                .param("start", (page - 1) * size)
                .param("count", size)
                .param("keyword", keyword)
                .query(Problem.class)
                .list();
    }

    public List<Problem> selectAllEnabled(Integer page, Integer size, String keyword) {
        return client.sql("""
                        select sql_calc_found_rows problem_id,
                                                   title,
                                                   enable,
                                                   category,
                                                   score,
                                                   create_at
                        from problem
                        where deleted = false
                          and enable = true
                          and if(:keyword is not null,
                                 title like concat('%', :keyword, '%') or find_in_set(:keyword, category),
                                 true)
                        limit :start, :count
                        """)
                .param("start", (page - 1) * size)
                .param("count", size)
                .param("keyword", keyword)
                .query(Problem.class)
                .list();
    }

    /**
     * 检查题目是否在竞赛中
     *
     * @param pid 题目 Id
     * @return 竞赛 Id
     */
    public Optional<Integer> isInContest(Integer pid) {
        return client.sql("""
                        select contest_id
                        from `contest-problem`
                        where problem_id = :pid
                        """)
                .param("pid", pid)
                .query(Integer.class)
                .optional();
    }

    /**
     * 检查题目是否开放
     *
     * @param pid 题目 Id
     * @return <code>true</code>：开放，<code>false</code>：禁用
     */
    public Optional<Boolean> isEnable(Integer pid) {
        return client.sql("""
                        select enable
                        from problem
                        where problem_id = :pid
                        """)
                .param("pid", pid)
                .query(Boolean.class)
                .optional();
    }

    public Integer updateEnable(Integer pid, Boolean enable) {
        return client.sql("""
                        update problem
                        set enable = :enable
                        where problem_id = :pid
                        """)
                .param("pid", pid)
                .param("enable", enable)
                .update();
    }

    public Integer insert(Problem problem) {
        return client.sql("""
                        insert into problem(title,
                                            description,
                                            category,
                                            score,
                                            timeout,
                                            memory_limit,
                                            output_limit)
                                    value (:title,
                                           :description,
                                           :category,
                                           :score,
                                           :timeout,
                                           :memoryLimit,
                                           :outputLimit)
                        """)
                .paramSource(problem)
                .update();
    }

    public Integer update(Problem problem) {
        return client.sql("""
                        update problem
                        set title        = :title,
                            description  = :description,
                            score        = :score,
                            timeout      = :timeout,
                            memory_limit = :memoryLimit,
                            output_limit = :outputLimit,
                            category     = :category
                        where problem_id = :problemId
                        """)
                .paramSource(problem)
                .update();
    }

    public Integer delete(Integer pid) {
        return client.sql("""
                        update problem
                        set deleted = true
                        where problem_id = :pid
                        """)
                .param("pid", pid)
                .update();
    }
}

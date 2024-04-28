package cloud.oj.core.repo;

import cloud.oj.core.entity.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ProblemRepo {

    private final DatabaseClient client;

    public Mono<Problem> selectById(Integer pid, Boolean onlyEnable) {
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
                .bind("onlyEnable", onlyEnable)
                .bind("pid", pid)
                .mapProperties(Problem.class)
                .first();
    }

    public Flux<Problem> selectAll(Integer page, Integer size, String keyword) {
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
                .bind("start", (page - 1) * size)
                .bind("count", size)
                .bind("keyword", keyword)
                .mapProperties(Problem.class)
                .all();
    }

    public Flux<Problem> selectAllEnabled(Integer page, Integer size, String keyword) {
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
                .bind("start", (page - 1) * size)
                .bind("count", size)
                .bind("keyword", keyword)
                .mapProperties(Problem.class)
                .all();
    }

    /**
     * 检查题目是否在竞赛中
     *
     * @param pid 题目 Id
     * @return 竞赛 Id
     */
    public Mono<Integer> isInContest(Integer pid) {
        return client.sql("""
                        select contest_id
                        from `contest-problem`
                        where problem_id = :pid
                        """)
                .bind("pid", pid)
                .mapValue(Integer.class)
                .first();
    }

    /**
     * 检查题目是否开放
     *
     * @param pid 题目 Id
     * @return <code>true</code>：开放，<code>false</code>：禁用
     */
    public Mono<Boolean> isEnable(Integer pid) {
        return client.sql("""
                        select 1
                        from problem
                        where problem_id = :pid and enable = true
                        """)
                .bind("pid", pid)
                .mapValue(Boolean.class)
                .first();
    }

    public Mono<Long> updateEnable(Integer pid, Boolean enable) {
        return client.sql("""
                        update problem
                        set enable = :enable
                        where problem_id = :pid
                        """)
                .bind("pid", pid)
                .bind("enable", enable)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> insert(Problem problem) {
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
                .bindProperties(problem)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> update(Problem problem) {
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
                .bindProperties(problem)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> delete(Integer pid) {
        return client.sql("""
                        update problem
                        set deleted = true
                        where problem_id = :pid
                        """)
                .bind("pid", pid)
                .fetch()
                .rowsUpdated();
    }
}

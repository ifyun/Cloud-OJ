package cloud.oj.core.repo;

import cloud.oj.core.entity.DataConf;
import cloud.oj.core.entity.Problem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProblemRepo {

    private final ObjectMapper mapper;

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
                          and if(LENGTH(:keyword) > 0,
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
     * 查询指定题目的测试数据配置
     *
     * @param pid 题目 Id
     * @return {@link Map}
     */
    public Optional<Map<String, Integer>> selectDataConf(Integer pid) {
        return client.sql("""
                        select * from data_conf where problem_id = :pid
                        """)
                .param("pid", pid)
                .query((rs, rowNum) -> {
                    Map<String, Integer> conf;

                    try {
                        conf = mapper.readValue(rs.getString("conf"), new TypeReference<>() {
                        });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    return conf;
                })
                .optional();
    }

    /**
     * 保存测试数据配置
     *
     * @param conf {@link DataConf}
     */
    @SneakyThrows
    public Integer updateDataConf(DataConf conf) {
        var pid = conf.getProblemId();
        var json = mapper.writeValueAsString(conf.getConf());
        return client.sql("""
                        replace into data_conf(problem_id, conf) values (:pid, :conf)
                        """)
                .param("pid", pid)
                .param("conf", json)
                .update();
    }

    /**
     * 从测试数据配置移除条目
     *
     * @param pid      题目 Id
     * @param fileName 文件名
     */
    public void removeFromDataConf(Integer pid, String fileName) {
        var sql = """
                select JSON_REMOVE(conf, CONCAT('$.', :name))
                from data_conf
                where problem_id = :pid
                """;
        client.sql(sql)
                .param("pid", pid)
                .param("name", fileName)
                .query();
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

    public Integer updateScore(Integer pid, Integer score) {
        return client.sql("""
                        update problem
                        set score = :score
                        where problem_id = :pid
                        """)
                .param("pid", pid)
                .param("score", score)
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

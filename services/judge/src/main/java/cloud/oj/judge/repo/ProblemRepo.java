package cloud.oj.judge.repo;

import cloud.oj.judge.entity.Problem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProblemRepo {

    private final ObjectMapper mapper;

    private final JdbcClient client;

    /**
     * 查询题目的资源限制
     *
     * @param pid 题目 Id
     * @return {@link Problem}
     */
    public Problem selectById(int pid) {
        return client.sql("""
                        select timeout,
                               memory_limit,
                               output_limit,
                               score
                        from problem
                        where problem_id = :pid
                        """)
                .param("pid", pid)
                .query(Problem.class)
                .single();
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
     * 检查题目是否开放
     *
     * @param pid 题目 Id
     * @return {@link Boolean}
     */
    public Boolean isEnable(int pid) {
        return client.sql("select enable from problem where problem_id = :pid")
                .param("pid", pid)
                .query(Boolean.class)
                .single();
    }
}

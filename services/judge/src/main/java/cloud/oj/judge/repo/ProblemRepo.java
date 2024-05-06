package cloud.oj.judge.repo;

import cloud.oj.judge.entity.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProblemRepo {

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

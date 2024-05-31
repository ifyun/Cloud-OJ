package cloud.oj.judge.repo;

import cloud.oj.judge.entity.Solution;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class SolutionRepo {

    private final JdbcClient client;

    /**
     * 从记录找出指定题目的历史最高分
     *
     * @param uid 用户 Id
     * @param pid 题目 Id
     * @param cid 竞赛 Id，可为空
     * @return {@link Double} 分数
     */
    public Double selectMaxScoreOfUser(Integer uid, Integer pid, Integer cid) {
        return client.sql("""
                        select max(score) as score
                        from solution
                        where uid = :uid
                          and problem_id = :pid
                          and if(:cid is not null, contest_id = :cid, contest_id is null)
                        """)
                .param("uid", uid)
                .param("pid", pid)
                .param("cid", cid)
                .query(Double.class)
                .single();
    }

    /**
     * 创建一个空的题解
     *
     * <p>生成的主键会保存到 <code>solution</code> 对象</p>
     *
     * @param solution {@link Solution}
     */
    public void insert(Solution solution) {
        var keyHolder = new GeneratedKeyHolder();
        var sql = """
                insert into solution(uid, problem_id, contest_id, title, language, state, submit_time)
                select :uid,
                       :problemId,
                       :contestId,
                       title,
                       :language,
                       :state,
                       :submitTime
                from problem
                where problem_id = :problemId
                """;
        client.sql(sql).paramSource(solution).update(keyHolder);
        solution.setId(Objects.requireNonNull(keyHolder.getKey()).toString());
    }

    /**
     * 更新状态
     *
     * @param sid   题解 Id
     * @param state 状态
     */
    public void updateState(String sid, Integer state) {
        client.sql("update solution set state = :state where solution_id = :sid")
                .param("sid", sid)
                .param("state", state)
                .update();
    }

    /**
     * 更新判题结果
     *
     * @param solution 带运行结果的 {@link Solution}
     */
    public void updateResult(Solution solution) {
        var sql = """
                update solution
                set state      = :state,
                    result     = :result,
                    total      = :total,
                    passed     = :passed,
                    pass_rate  = :passRate,
                    score      = :score,
                    time       = :time,
                    memory     = :memory,
                    error_info = :errorInfo
                where solution_id = :solutionId
                """;
        client.sql(sql)
                .paramSource(solution)
                .update();
    }
}

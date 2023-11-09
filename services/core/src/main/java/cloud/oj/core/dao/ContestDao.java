package cloud.oj.core.dao;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.entity.ProblemOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ContestDao {
    @Select("""
            select invite_key
            from contest
            where contest_id = #{contestId}
            """)
    String getInviteKey(Integer contestId);

    @Update("""
            update contest
            set invite_key = #{key}
            where contest_id = #{contestId}
            """)
    void newInviteKey(Integer contestId, String key);

    @Select("""
            select problem_id,
                   `order`
            from `contest-problem`
            where contest_id = #{cid}
            order by problem_id
            """
    )
    List<ProblemOrder> getProblemOrders(Integer cid);

    /**
     * 改变竞赛中题目的顺序
     */
    @Update("""
            update `contest-problem`
            set `order` = #{order}
            where contest_id = #{cid}
              and problem_id = #{pid}
            """)
    void setProblemOrder(Integer cid, Integer pid, Integer order);

    @Select("""
            select count(problem_id)
            from `contest-problem`
            where contest_id = #{cid}
            """)
    Integer countProblems(Integer cid);

    Contest getState(Integer contestId);

    List<List<?>> getAll(boolean admin, int start, int limit);

    List<List<?>> getStarted(int start, int limit);

    List<List<?>> getProblemsNotInContest(Integer contestId, int start, int limit);

    List<Problem> getProblems(Integer contestId);

    List<Problem> getProblemsInStarted(Integer contestId);

    Contest getContestById(Integer contestId);

    int createContest(Contest contest);

    int updateContest(Contest contest);

    int deleteContest(Integer contestId);

    int addProblem(Integer contestId, Integer problemId);

    int removeProblem(Integer contestId, Integer problemId);
}

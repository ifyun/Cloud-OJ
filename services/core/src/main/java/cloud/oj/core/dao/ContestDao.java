package cloud.oj.core.dao;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.Problem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ContestDao {
    @Select("""
            select invite_key
            from contest
            where contest_id = #{contestId}
            """)
    String getInviteKey(Integer contestId);

    Contest getState(Integer contestId);

    List<List<?>> getAll(int start, int limit);

    List<List<?>> getStarted(int start, int limit);

    List<List<?>> getProblemsNotInContest(Integer contestId, int start, int limit);

    List<Problem> getProblems(Integer contestId);

    List<Problem> getProblemsInStarted(Integer contestId);

    List<Problem> getProblemsWithResult(Integer uid, Integer contestId);

    Contest getContestById(Integer contestId);

    int createContest(Contest contest);

    int updateContest(Contest contest);

    int deleteContest(Integer contestId);

    int addProblem(Integer contestId, Integer problemId);

    int removeProblem(Integer contestId, Integer problemId);
}

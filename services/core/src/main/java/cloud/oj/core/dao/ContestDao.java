package cloud.oj.core.dao;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.Problem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContestDao {
    boolean isContestEnded(int contestId);

    List<List<?>> getContests(int start, int limit, boolean onlyStarted);

    List<Problem> getProblems(String userId, int contestId, boolean onlyStarted);

    List<List<?>> getProblemsNotInContest(int contestId, int start, int limit);

    Contest getContest(int contestId);

    int addContest(Contest contest);

    int updateContest(Contest contest);

    int deleteContest(int contestId);

    int addProblem(int contestId, int problemId);

    int removeProblem(int contestId, int problemId);
}
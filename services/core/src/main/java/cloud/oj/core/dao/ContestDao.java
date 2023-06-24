package cloud.oj.core.dao;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.Problem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContestDao {
    Contest getState(int contestId);

    List<List<?>> getContests(int start, int limit, boolean onlyStarted);

    List<Problem> getProblems(int contestId, boolean onlyStarted);

    List<Problem> getProblemsWithResult(String userId, int contestId);

    List<List<?>> getProblemsNotInContest(int contestId, int start, int limit);

    Contest getContestById(int contestId);

    int addContest(Contest contest);

    int updateContest(Contest contest);

    int deleteContest(int contestId);

    int addProblem(int contestId, int problemId);

    int removeProblem(int contestId, int problemId);
}

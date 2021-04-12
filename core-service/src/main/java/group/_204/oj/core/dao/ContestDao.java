package group._204.oj.core.dao;

import group._204.oj.core.model.Contest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContestDao {
    boolean isContestEnded(int contestId);

    List<List<?>> getContests(int start, int limit, boolean onlyStarted);

    List<List<?>> getProblems(String userId, int contestId, boolean onlyStarted, int start, int limit);

    List<List<?>> getProblemsNotInContest(int contestId, int start, int limit);

    Contest getContest(int contestId);

    int addContest(Contest contest);

    int updateContest(Contest contest);

    int deleteContest(int contestId);

    int addProblem(int contestId, int problemId);

    int removeProblem(int contestId, int problemId);
}

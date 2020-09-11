package group._204.oj.manager.dao;

import group._204.oj.manager.model.Contest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContestDao {

    List<List<?>> getAllContest(int start, int limit);

    List<List<?>> getStartedContest(int start, int limit);

    List<List<?>> getProblems(String userId, int contestId, int start, int limit);

    List<List<?>> getProblemsNotInContest(int contestId, int start, int limit);

    Contest getContest(int contestId);

    int addContest(Contest contest);

    int updateContest(Contest contest);

    int deleteContest(int contestId);

    int addProblem(int contestId, int problemId);

    int deleteProblem(int contestId, int problemId);
}

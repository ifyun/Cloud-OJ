package top.cloudli.managerservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.managerservice.model.Contest;
import top.cloudli.managerservice.model.Problem;

import java.util.List;

@Mapper
public interface ContestDao {
    long getAllCount();

    List<Contest> getAllContest(int start, int limit);

    long getStartedCount();

    List<Contest> getStartedContest(int start, int limit);

    long getProblemsCount(int contestId);

    List<Problem> getProblems(int contestId, int start, int limit);

    int addContest(Contest contest);

    int updateContest(Contest contest);

    int deleteContest(int contestId);

    int addProblem(int contestId, int problemId);

    int deleteProblem(int contestId, int problemId);
}

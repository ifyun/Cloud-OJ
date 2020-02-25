package top.cloudli.managerservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.managerservice.model.Contest;

import java.util.List;

@Mapper
public interface ContestDao {

    List<List<?>> getAllContest(int start, int limit);

    List<List<?>> getStartedContest(int start, int limit);

    List<List<?>> getProblems(String userId, int contestId, int start, int limit);

    List<List<?>> getProblemsNotInContest(int contestId, int start, int limit);

    int addContest(Contest contest);

    int updateContest(Contest contest);

    int deleteContest(int contestId);

    int addProblem(int contestId, int problemId);

    int deleteProblem(int contestId, int problemId);
}

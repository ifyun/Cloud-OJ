package cloud.oj.core.dao;

import cloud.oj.core.entity.Contest;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.entity.ProblemOrder;

import java.util.List;

public interface ContestDao {

    String getInviteKey(Integer contestId);

    void newInviteKey(Integer contestId, String key);

    List<ProblemOrder> getProblemOrders(Integer cid);

    void setProblemOrder(Integer cid, Integer pid, Integer order);

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

package cloud.oj.core.service;

import cloud.oj.core.dao.ContestDao;
import cloud.oj.core.dao.ProblemDao;
import cloud.oj.core.dao.SolutionDao;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.error.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProblemService {
    private final ProblemDao problemDao;

    private final ContestDao contestDao;

    private final SolutionDao solutionDao;

    public ProblemService(ProblemDao problemDao, ContestDao contestDao, SolutionDao solutionDao) {
        this.problemDao = problemDao;
        this.contestDao = contestDao;
        this.solutionDao = solutionDao;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<List<?>> getAllEnabled(Integer uid, String keyword, int page, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = null;
        }

        var data = problemDao.getAllEnabled((page - 1) * limit, limit, keyword);

        if (uid != null) {
            data.get(0).forEach((e) -> {
                var p = (Problem) e;
                p.setResult(solutionDao.getResult(uid, p.getProblemId()));
            });
        }

        return data;
    }

    public List<List<?>> getAll(String keyword, int page, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = null;
        }

        return problemDao.getAll((page - 1) * limit, limit, keyword);
    }

    public Optional<Problem> getSingle(int problemId) {
        return Optional.ofNullable(problemDao.getById(problemId, false));
    }

    public Optional<Problem> getSingleEnabled(int problemId) {
        return Optional.ofNullable(problemDao.getById(problemId, true));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public HttpStatus update(Problem problem) {
        var categories = problem.getCategory().split(",");
        // 对标签排序
        if (categories.length > 1) {
            Arrays.sort(categories);
            problem.setCategory(StringUtils.join(categories, ","));
        }

        if (problemDao.update(problem) == 1) {
            // 更新 solution 中的标题
            solutionDao.updateTitle(problem.getTitle(), problem.getProblemId());
            return HttpStatus.OK;
        } else {
            var msg = String.format("题目(%d)更新失败", problem.getProblemId());
            throw new GenericException(HttpStatus.BAD_REQUEST, msg);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public HttpStatus setEnable(int problemId, boolean enable) {
        if (enable) {
            var contestId = problemDao.isInContest(problemId);
            var contest = contestDao.getContestById(contestId);

            if (contest != null && !contest.isEnded()) {
                throw new GenericException(HttpStatus.BAD_REQUEST, "不准开放未结束竞赛中的题目");
            }
        }

        if (problemDao.setEnable(problemId, enable) == 1) {
            return HttpStatus.OK;
        } else {
            throw new GenericException(HttpStatus.BAD_REQUEST, String.format("题目(%d)开放/关闭失败", problemId));
        }
    }

    public HttpStatus create(Problem problem) {
        var categories = problem.getCategory().split(",");
        // 对标签排序
        if (categories.length > 1) {
            Arrays.sort(categories);
            problem.setCategory(StringUtils.join(categories, ","));
        }

        if (problemDao.add(problem) == 1) {
            return HttpStatus.CREATED;
        } else {
            throw new GenericException(HttpStatus.BAD_REQUEST, "无法创建题目");
        }
    }

    public HttpStatus delete(Integer problemId) {
        if (problemDao.isInContest(problemId) != null) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "不准删除竞赛中的题目");
        }

        if (problemDao.isEnable(problemId)) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "不准删除已开放的题目");
        }

        if (problemDao.delete(problemId) == 1) {
            return HttpStatus.NO_CONTENT;
        } else {
            throw new GenericException(HttpStatus.GONE, String.format("题目(%d)删除失败", problemId));
        }
    }
}

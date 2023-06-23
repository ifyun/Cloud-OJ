package cloud.oj.core.service;

import cloud.oj.core.dao.ContestDao;
import cloud.oj.core.dao.ProblemDao;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.error.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProblemService {
    private final ProblemDao problemDao;

    private final ContestDao contestDao;

    @Autowired
    public ProblemService(ProblemDao problemDao, ContestDao contestDao) {
        this.problemDao = problemDao;
        this.contestDao = contestDao;
    }

    public List<List<?>> getAllEnabled(String keyword, int page, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            return problemDao.getAllEnabled((page - 1) * limit, limit, null);
        }

        return problemDao.getAllEnabled((page - 1) * limit, limit, keyword);
    }

    public List<List<?>> getAll(String keyword, int page, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            problemDao.getAll((page - 1) * limit, limit, null);
        }

        return problemDao.getAll((page - 1) * limit, limit, keyword);
    }

    public Optional<Problem> getSingle(int problemId) {
        return Optional.ofNullable(problemDao.getSingle(problemId, false));
    }

    public Optional<Problem> getSingleEnabled(int problemId) {
        return Optional.ofNullable(problemDao.getSingle(problemId, true));
    }

    @Transactional
    public Integer update(Problem problem) {
        var contestId = problemDao.isInContest(problem.getProblemId());

        if (contestId != null && contestDao.getContest(contestId).isStarted()) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "不能修改已开始竞赛中的题目");
        }

        if (problemDao.update(problem) == 1) {
            return HttpStatus.OK.value();
        } else {
            throw new GenericException(
                    HttpStatus.NOT_MODIFIED.value(),
                    String.format("题目 %d 更新失败", problem.getProblemId())
            );
        }
    }

    @Transactional
    public Integer toggleEnable(int problemId, boolean enable) {
        if (enable) {
            var contestId = problemDao.isInContest(problemId);

            if (contestId != null && !contestDao.getContest(contestId).isEnded()) {
                throw new GenericException(HttpStatus.BAD_REQUEST.value(), "不准开放未结束竞赛中的题目");
            }
        }

        if (problemDao.toggleEnable(problemId, enable) == 1) {
            return HttpStatus.OK.value();
        } else {
            throw new GenericException(
                    HttpStatus.NOT_MODIFIED.value(),
                    String.format("题目 %d 开放/关闭失败", problemId)
            );
        }
    }

    public Integer add(Problem problem) {
        if (problemDao.add(problem) == 1) {
            return HttpStatus.CREATED.value();
        } else {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "请求数据可能不正确");
        }
    }

    public Integer delete(Integer problemId) {
        if (problemDao.isInContest(problemId) != null) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "不准删除竞赛中的题目");
        }

        if (problemDao.isEnable(problemId)) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "不准删除已开放的题目");
        }

        if (problemDao.delete(problemId) == 1) {
            return HttpStatus.NO_CONTENT.value();
        } else {
            throw new GenericException(HttpStatus.GONE.value(), String.format("题目 %d 删除失败", problemId));
        }
    }
}

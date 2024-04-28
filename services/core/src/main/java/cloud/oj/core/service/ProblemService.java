package cloud.oj.core.service;

import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.Problem;
import cloud.oj.core.error.GenericException;
import cloud.oj.core.repo.CommonRepo;
import cloud.oj.core.repo.ContestRepo;
import cloud.oj.core.repo.ProblemRepo;
import cloud.oj.core.repo.SolutionRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final CommonRepo commonRepo;

    private final ProblemRepo problemRepo;

    private final ContestRepo contestRepo;

    private final SolutionRepo solutionRepo;

    /**
     * 分页查询所有开放的题目
     *
     * @param uid     用户 Id，用于查询判题结果
     * @param keyword 关键字，用于搜索题目
     * @param page    页数
     * @param size    每页数量
     * @return {@link PageData} of {@link Problem}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<PageData<Problem>> getAllEnabled(Integer uid, String keyword, int page, int size) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = null;
        }

        return problemRepo.selectAllEnabled(page, size, keyword)
                .flatMap(p -> Mono.just(p).zipWith(solutionRepo.selectResult(uid, p.getProblemId())))
                .map(t -> t.getT1().setResultAndReturn(t.getT2()))
                .collectList()
                .zipWith(commonRepo.selectFoundRows())
                .map(t -> new PageData<>(t.getT1(), t.getT2()));
    }

    /**
     * 分页查询所有题目(Admin)
     *
     * @param keyword 关键字，用于搜索题目
     * @param page    页数
     * @param size    每页数量
     * @return {@link PageData} of {@link Problem}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<PageData<Problem>> getAll(String keyword, int page, int size) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = null;
        }

        return problemRepo.selectAll(page, size, keyword)
                .collectList()
                .zipWith(commonRepo.selectFoundRows())
                .map(t -> new PageData<>(t.getT1(), t.getT2()));
    }

    /**
     * 查询单个题目，包括未开放的题目
     *
     * @param pid 题目 Id
     * @return {@link Problem}
     */
    public Mono<Problem> getSingle(int pid) {
        return problemRepo.selectById(pid, false);
    }

    /**
     * 查询单个题目，仅限已开放
     *
     * @param pid 题目 Id
     * @return {@link Problem}
     */
    public Mono<Problem> getSingleEnabled(int pid) {
        return problemRepo.selectById(pid, true);
    }

    /**
     * 更新题目
     *
     * @param problem {@link Problem}
     * @return {@link HttpStatus}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<HttpStatus> update(Problem problem) {
        var categories = problem.getCategory().split(",");
        // 对标签排序
        if (categories.length > 1) {
            Arrays.sort(categories);
            problem.setCategory(StringUtils.join(categories, ","));
        }

        return problemRepo.update(problem)
                .flatMap(rows -> rows > 0 ?
                        solutionRepo.updateTitle(problem.getProblemId(), problem.getTitle()) :
                        Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "更新失败"))
                )
                .map(rows -> rows > 0 ?
                        HttpStatus.OK :
                        HttpStatus.BAD_REQUEST
                );
    }

    /**
     * 设置题目开放/关闭，若题目处于进行中的竞赛则禁止操作
     *
     * @param pid    题目 Id
     * @param enable 是否开放
     * @return {@link HttpStatus}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<HttpStatus> setEnable(int pid, boolean enable) {
        if (enable) {
            return problemRepo.isInContest(pid)
                    .flatMap(contestRepo::selectById)
                    .flatMap(contest -> contest.isEnded() ?
                            problemRepo.updateEnable(pid, true) :
                            Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "不准开放未结束竞赛中的题目"))
                    )
                    .map(rows -> rows > 0 ?
                            HttpStatus.OK :
                            HttpStatus.BAD_REQUEST
                    );
        }

        return problemRepo.updateEnable(pid, false)
                .map(rows -> rows > 0 ?
                        HttpStatus.OK :
                        HttpStatus.BAD_REQUEST
                );
    }

    /**
     * 创建题目
     *
     * @param problem {@link Problem}
     * @return {@link HttpStatus}
     */
    public Mono<HttpStatus> create(Problem problem) {
        var categories = problem.getCategory().split(",");
        // 对标签排序
        if (categories.length > 1) {
            Arrays.sort(categories);
            problem.setCategory(StringUtils.join(categories, ","));
        }

        return problemRepo.insert(problem)
                .flatMap(rows -> rows > 0 ?
                        Mono.just(HttpStatus.CREATED) :
                        Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "无法创建题目"))
                );
    }

    /**
     * 逻辑删除题目
     *
     * @param pid 题目 Id
     * @return {@link HttpStatus}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<HttpStatus> delete(Integer pid) {
        return problemRepo.isInContest(pid)
                .flatMap(cid -> Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "不准删除竞赛中的题目")))
                .then(problemRepo.isEnable(pid))
                .flatMap(enable -> enable ?
                        Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "不准删除已开放的题目")) :
                        problemRepo.delete(pid)
                )
                .map(rows -> rows > 0 ?
                        HttpStatus.NO_CONTENT :
                        HttpStatus.GONE
                );
    }
}

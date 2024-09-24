package cloud.oj.core.service;

import cloud.oj.core.config.AppConfig;
import cloud.oj.core.entity.DataConf;
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

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final AppConfig appConfig;

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
    public PageData<Problem> getAllEnabled(Integer uid, String keyword, int page, int size) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = null;
        }

        var data = problemRepo.selectAllEnabled(page, size, keyword);
        var total = commonRepo.selectFoundRows();

        data.forEach(p -> solutionRepo.selectResult(uid, p.getProblemId()).ifPresent(p::setResult));

        return new PageData<>(data, total);
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
    public PageData<Problem> getAll(String keyword, int page, int size) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = null;
        }

        var data = problemRepo.selectAll(page, size, keyword);
        var total = commonRepo.selectFoundRows();

        return new PageData<>(data, total);
    }

    /**
     * 查询单个题目，包括未开放的题目
     *
     * @param pid 题目 Id
     * @return {@link Problem}
     */
    public Problem getSingle(int pid) {
        var problem = problemRepo.selectById(pid, false);

        if (problem.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "找不到题目");
        }

        return problem.get();
    }

    /**
     * 查询单个题目，仅限已开放
     *
     * @param pid 题目 Id
     * @return {@link Problem}
     */
    public Problem getSingleEnabled(int pid) {
        var problem = problemRepo.selectById(pid, true);

        if (problem.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "找不到题目");
        }

        return problem.get();
    }

    /**
     * 更新题目
     *
     * @param problem {@link Problem}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void update(Problem problem) {
        var categories = problem.getCategory().split(",");
        // 对标签排序
        if (categories.length > 1) {
            Arrays.sort(categories);
            problem.setCategory(StringUtils.join(categories, ","));
        }

        problemRepo.update(problem);
        solutionRepo.updateTitle(problem.getProblemId(), problem.getTitle());
    }

    /**
     * 设置题目开放/关闭，若题目处于进行中的竞赛则禁止操作
     *
     * @param pid    题目 Id
     * @param enable 是否开放
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void setEnable(int pid, boolean enable) {
        if (enable) {
            problemRepo.isInContest(pid)
                    .flatMap(contestRepo::selectById)
                    .ifPresent(contest -> {
                        if (!contest.isEnded()) {
                            throw new GenericException(HttpStatus.BAD_REQUEST, "不准开放未结束竞赛中的题目");
                        }
                    });
        }

        if (problemRepo.updateEnable(pid, enable) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "操作失败");
        }
    }

    /**
     * 创建题目
     *
     * @param problem {@link Problem}
     */
    public void create(Problem problem) {
        var categories = problem.getCategory().split(",");
        // 对标签排序
        if (categories.length > 1) {
            Arrays.sort(categories);
            problem.setCategory(StringUtils.join(categories, ","));
        }

        problem.setScore(0);

        if (problemRepo.insert(problem) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "操作失败");
        }
    }

    /**
     * 更新测试数据配置
     *
     * @param conf {@link DataConf}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void saveDataConf(DataConf conf) {
        // ! 开放的题目不准修改数据配置
        var enable = problemRepo.isEnable(conf.getProblemId());

        if (enable.isEmpty()) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "题目不存在");
        }

        if (enable.get()) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "题目已开放，不准修改测试点分数");
        }

        // ! 检查测试点是否均设置了分数
        var pid = conf.getProblemId();
        var files = new File(appConfig.getFileDir() + "data/" + pid).listFiles();

        for (File file : Objects.requireNonNull(files)) {
            var name = file.getName();
            if (name.endsWith(".out")) {
                var score = conf.getConf().get(name);
                if (score == null || score == 0) {
                    throw new GenericException(HttpStatus.BAD_REQUEST, "分数配置不完整");
                }
            }
        }

        conf.getConf()
                .values()
                .stream()
                .reduce(Integer::sum)
                .ifPresent(value -> {
                    // 设置题目分数为测试点分数之和
                    if (problemRepo.updateDataConf(conf) == 0 ||
                            problemRepo.updateScore(pid, value) == 0) {
                        throw new GenericException(HttpStatus.BAD_REQUEST, "操作失败");
                    }
                });
    }

    /**
     * 逻辑删除题目
     *
     * @param pid 题目 Id
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public HttpStatus delete(Integer pid) {
        if (problemRepo.isEnable(pid).isEmpty()) {
            throw new GenericException(HttpStatus.GONE, "题目不存在");
        }

        if (problemRepo.isInContest(pid).isPresent()) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "不准删除竞赛中的题目");
        }

        return problemRepo.delete(pid) == 0 ?
                HttpStatus.GONE :
                HttpStatus.NO_CONTENT;
    }
}

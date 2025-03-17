package cloud.oj.core.service;

import cloud.oj.core.entity.*;
import cloud.oj.core.error.GenericException;
import cloud.oj.core.repo.CommonRepo;
import cloud.oj.core.repo.ContestRepo;
import cloud.oj.core.repo.ScoreboardRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.TreeSet;

/**
 * 排名相关业务
 */
@Service
@RequiredArgsConstructor
public class RankingService {

    private final CommonRepo commonRepo;

    private final ScoreboardRepo scoreboardRepo;

    private final ContestRepo contestRepo;

    private final SystemSettings systemSettings;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PageData<Ranking> getRanking(int page, int size, boolean admin) {
        var data =  scoreboardRepo.selectAll(page, size, admin);
        var total = commonRepo.selectFoundRows();

        return new PageData<>(data, total);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RankingContest getContestRanking(Integer cid, boolean admin) {
        var contest = contestRepo.selectById(cid);

        if (contest.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "竞赛不存在");
        }

        if (!systemSettings.getSettings().isAlwaysShowRanking() && !contest.get().isEnded()) {
            throw new GenericException(HttpStatus.FORBIDDEN, "结束后才可查看");
        }

        var data = new RankingContest(contest.get().withoutKey());      // 排名顶层包装
        var problemOrders = contestRepo.selectOrders(cid);              // 题目 id 和 order
        var ranking = scoreboardRepo.selectByCid(cid, admin);                  // 排名

        ranking.forEach((r) -> {
            var detailList = scoreboardRepo.selectScores(r.getUid(), cid);
            // 取 order 字段到 detailList
            detailList.forEach(d -> {
                var order = problemOrders.stream().filter(p -> d.getProblemId().equals(p.getProblemId()))
                        .findAny()
                        // 理论上不会为 null
                        .orElse(new ProblemOrder(0))
                        .getOrder();
                d.setOrder(order);
            });
            // 使用 Set 合并没有提交过的题目，按 order 字段排序
            TreeSet<ScoreDetail> detailSet = new TreeSet<>(detailList);
            problemOrders.forEach((p) -> detailSet.add(new ScoreDetail(p.getProblemId(), p.getOrder())));
            r.setDetails(detailSet);
        });

        // 取 problemId，按 order 字段排序
        var idList = problemOrders.stream().sorted().map(ProblemOrder::getProblemId).toList();

        data.setProblemIds(idList);
        data.setRanking(ranking);

        return data;
    }
}

package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RankingContest {
    private Contest contest;
    private List<Integer> problemIds;
    private List<Ranking> ranking;

    public RankingContest(Contest contest) {
        this.contest = contest;
    }
}

package cloud.oj.core.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("unused")
public class ScoreDetail implements Comparable<ScoreDetail> {
    private Integer problemId;
    private Integer result;
    private Double score;

    public ScoreDetail(Integer problemId) {
        this.problemId = problemId;
    }

    @Override
    public int compareTo(ScoreDetail o) {
        return this.problemId.compareTo(o.problemId);
    }
}

package cloud.oj.core.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScoreDetail implements Comparable<ScoreDetail> {
    private Integer problemId;
    private Integer result;
    private Double score;

    @JsonUnwrapped
    private Integer order;

    public ScoreDetail(Integer problemId, Integer order) {
        this.problemId = problemId;
        this.order = order;
    }

    /**
     * <p>按 order 排序，相同则按 problemId 排序</p>
     */
    @Override
    public int compareTo(ScoreDetail o) {
        var r = this.order.compareTo(o.order);
        return  r == 0 ? this.problemId.compareTo(o.problemId) : r;
    }
}

package cloud.oj.core.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProblemOrder implements Comparable<ProblemOrder> {
    private Integer problemId;
    private Integer order;

    public ProblemOrder(Integer order) {
        this.order = order;
    }

    @Override
    public int compareTo(ProblemOrder o) {
        return this.order.compareTo(o.order);
    }
}

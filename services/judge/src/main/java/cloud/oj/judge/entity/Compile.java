package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Compile {
    private Integer id;
    private Integer solutionId;
    private Integer state;
    private String info;

    public Compile(Integer solutionId, Integer state) {
        this(solutionId, state, null);
    }

    public Compile(Integer solutionId, Integer state, String info) {
        this.solutionId = solutionId;
        this.state = state;
        this.info = info;
    }
}

package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Compile {
    private int id;
    private String solutionId;
    private int state;
    private String info;

    public Compile(String solutionId, int state) {
        this(solutionId, state, null);
    }

    public Compile(String solutionId, int state, String info) {
        this.solutionId = solutionId;
        this.state = state;
        this.info = info;
    }
}

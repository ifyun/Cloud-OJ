package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class Compile {
    private Integer id;
    private BigInteger solutionId;
    private Integer state;
    private String info;

    public Compile(String solutionId, Integer state) {
        this(solutionId, state, null);
    }

    public Compile(String solutionId, Integer state, String info) {
        this.solutionId = new BigInteger(solutionId);
        this.state = state;
        this.info = info;
    }
}

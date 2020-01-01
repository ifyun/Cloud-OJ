package group._204.oj.judge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Runtime {
    private int id;
    private String solutionId;
    private int total;
    private int passed;
    private long time;
    private int result;
    private String info;
    private String output;

    public Runtime(String solutionId) {
        this(0, solutionId, 0, 0, 0, 0, null, null);
    }
}

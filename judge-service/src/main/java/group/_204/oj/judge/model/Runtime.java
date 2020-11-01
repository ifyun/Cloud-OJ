package group._204.oj.judge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Runtime {
    private Integer id;
    private String solutionId;
    private int total;
    private Long passed;
    private Long time;
    private Long memory;
    private int result;
    private String info;

    public Runtime(String solutionId) {
        this(null, solutionId, 0, 0L, null, null, 0, null);
    }
}

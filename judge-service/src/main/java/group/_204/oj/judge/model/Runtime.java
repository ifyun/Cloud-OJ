package group._204.oj.judge.model;

import group._204.oj.judge.type.SolutionResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Runtime {
    private Integer id;
    private String solutionId;
    private Integer total;
    private Integer passed;
    private Long time;
    private Long memory;
    private SolutionResult result;
    private String info;

    public Runtime(String solutionId) {
        this(null, solutionId, 0, 0, null, null, null, null);
    }
}

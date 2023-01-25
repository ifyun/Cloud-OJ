package cloud.oj.judge.entity;

import cloud.oj.judge.enums.SolutionResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

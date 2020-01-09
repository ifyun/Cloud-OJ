package top.cloudli.judgeservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Runtime {
    private int id;
    private int solutionId;
    private int total;
    private int passed;

    public Runtime(int solutionId, int total, int passed) {
        this(0, solutionId, total, passed);
    }
}

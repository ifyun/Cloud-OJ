package cloud.oj.judge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunResult {
    private Integer status;
    private String result;
    private Integer total;
    private Integer passed;
    private Double passRate;
    private Long time;
    private Long memory;
}

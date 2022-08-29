package cloud.oj.judge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

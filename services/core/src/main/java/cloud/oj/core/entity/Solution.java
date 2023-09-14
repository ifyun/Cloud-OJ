package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Solution {
    private Integer solutionId;
    private Integer problemId;
    private String title;
    private Integer uid;
    private Integer passed;
    private Integer total;
    private Double passRate;
    private Double score;
    private Long time;
    private Long memory;
    private String errorInfo;
    private Integer language;
    private Integer state;
    private Integer result;
    // UNIX 时间戳(13 位)
    private Long submitTime;
}

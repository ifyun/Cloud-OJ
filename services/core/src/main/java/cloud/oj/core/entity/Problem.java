package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Problem {
    private Integer contestId;
    private String contestName;
    private Long startAt;
    private Long endAt;
    private Integer problemId;
    private Integer result;
    private Integer passed;
    private String title;
    private String description;
    private String category;
    private Long timeout;
    private Integer memoryLimit;
    private Integer outputLimit;
    private Integer languages;
    private Integer score;
    private Boolean enable;
    // UNIX 时间戳(10 位)
    private Long createAt;
}

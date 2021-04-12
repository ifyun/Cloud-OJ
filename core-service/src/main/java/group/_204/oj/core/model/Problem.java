package group._204.oj.core.model;

import lombok.Data;

import java.util.Date;

@Data
public class Problem {
    private Integer contestId;
    private String contestName;
    private Date startAt;
    private Date endAt;
    private Integer problemId;
    private Integer result;
    private Integer passed;
    private String title;
    private String description;
    private String sql;
    private String category;
    private Long timeout;
    private Integer memoryLimit;
    private Integer languages;
    private Integer type;
    private Integer score;
    private Boolean enable;
    private Date createAt;
    private TestData testData;
}

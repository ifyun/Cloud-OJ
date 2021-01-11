package group._204.oj.manager.model;

import lombok.Data;

import java.sql.Date;

@Data
public class JudgeResult {
    private String solutionId;
    private Integer problemId;
    private String title;
    private String userId;
    private Double passRate;
    private Double score;
    private Long time;
    private Long memory;
    private String errorInfo;
    private String code;
    private Integer language;
    private Integer state;
    private Integer result;
    private Date submitTime;
}

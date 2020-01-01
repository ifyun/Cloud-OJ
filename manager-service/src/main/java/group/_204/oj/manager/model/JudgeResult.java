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
    private String code;
    private String compileInfo;
    private Integer language;
    private Integer result;
    private Date submitTime;
}

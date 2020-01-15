package top.cloudli.managerservice.model;

import lombok.Data;

import java.sql.Date;

@Data
public class JudgeResult {
    private int solutionId;
    private int problemId;
    private String title;
    private String userId;
    private double passRate;
    private double score;
    private long time;
    private String code;
    private String compileInfo;
    private String language;
    private String result;
    private Date submitTime;
}

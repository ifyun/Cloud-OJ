package group._204.oj.judge.model;

import group._204.oj.judge.type.SolutionResult;
import group._204.oj.judge.type.SolutionState;
import lombok.Data;

import java.sql.Date;

@Data
public class JudgeResult {
    private String solutionId;
    private Integer problemId;
    private String userId;
    private Integer language;
    private SolutionState state;
    private SolutionResult result;
    private Integer compileState;
    private String compileInfo;
    private double passRate;
    private double score;
    private long time;
    private long memory;
    private Date submitTime;
}

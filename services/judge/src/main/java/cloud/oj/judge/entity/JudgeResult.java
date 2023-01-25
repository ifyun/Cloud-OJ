package cloud.oj.judge.entity;

import cloud.oj.judge.enums.SolutionResult;
import cloud.oj.judge.enums.SolutionState;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
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
    private Long time;
    private Long memory;
    private String errorInfo;
    private Date submitTime;
}

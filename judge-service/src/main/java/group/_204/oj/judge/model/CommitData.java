package group._204.oj.judge.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommitData implements Serializable {
    private String solutionId;
    private String userId;
    private Integer problemId;
    private Integer contestId;
    private String sourceCode;
    private Integer language;
    private Integer type;
}

package group._204.oj.judge.model;

import group._204.oj.judge.type.SolutionResult;
import group._204.oj.judge.type.SolutionState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Solution {
    private String solutionId;
    private Integer problemId;
    private Integer contestId;
    private String userId;
    private Integer language;
    private SolutionState state;
    private SolutionResult result;
    private double passRate;
    private double score;
    private String sourceCode;
    private Integer type;
    private Date submitTime;

    public Solution(String solutionId, String userId, Integer problemId, Integer contestId,
                    Integer language, int type, Date submitTime) {
        this.solutionId = solutionId;
        this.userId = userId;
        this.problemId = problemId;
        this.contestId = contestId;
        this.language = language;
        this.type = type;
        this.submitTime = submitTime;
    }
}

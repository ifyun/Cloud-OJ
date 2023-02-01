package cloud.oj.judge.entity;

import cloud.oj.judge.enums.SolutionResult;
import cloud.oj.judge.enums.SolutionState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
    private Long submitTime;

    public Solution(String solutionId, String userId, Integer problemId, Integer contestId,
                    Integer language, Long submitTime) {
        this.solutionId = solutionId;
        this.userId = userId;
        this.problemId = problemId;
        this.contestId = contestId;
        this.language = language;
        this.submitTime = submitTime;
    }
}

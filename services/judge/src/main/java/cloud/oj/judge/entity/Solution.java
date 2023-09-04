package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static cloud.oj.judge.component.SolutionState.JUDGED;

@Getter
@Setter
@NoArgsConstructor
public class Solution {
    private String solutionId;
    private Integer uid;
    private Integer problemId;
    private Integer contestId;
    private Integer language;
    private Integer state;
    private Integer result;
    private Integer total = 0;
    private Integer passed = 0;
    private Double passRate = 0D;
    private Double score = 0D;
    private Long time = 0L;
    private Long memory = 0L;
    private String errorInfo;
    private Long submitTime;
    private String sourceCode;

    public Solution(String solutionId, Integer uid, Integer problemId, Integer contestId,
                    Integer language, Long submitTime) {
        this.solutionId = solutionId;
        this.uid = uid;
        this.problemId = problemId;
        this.contestId = contestId;
        this.language = language;
        this.submitTime = submitTime;
    }

    public void endWithError(Integer result, String info) {
        this.result = result;
        state = JUDGED;
        errorInfo = info;
    }
}

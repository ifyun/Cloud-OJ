package cloud.oj.judge.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static cloud.oj.judge.component.SolutionState.JUDGED;

@Getter
@Setter
@NoArgsConstructor
public class Solution {
    private String solutionId;
    private Integer problemId;
    private Integer contestId;
    private String userId;
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

    public Solution(String solutionId, String userId, Integer problemId, Integer contestId,
                    Integer language, Long submitTime) {
        this.solutionId = solutionId;
        this.userId = userId;
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

    @JsonGetter
    public Integer getStateValue() {
        if (state == null) {
            return null;
        }

        return state - 1;
    }

    @JsonGetter
    public Integer getResultValue() {
        if (result == null) {
            return null;
        }

        return result - 1;
    }
}

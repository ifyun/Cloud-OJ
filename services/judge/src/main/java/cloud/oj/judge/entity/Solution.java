package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

import static cloud.oj.judge.constant.State.JUDGED;
import static cloud.oj.judge.constant.State.WAITING;

@Getter
@Setter
@NoArgsConstructor
public class Solution {
    private BigInteger solutionId;
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
    // 用于队列，不属于数据库字段
    private String sourceCode;

    public Solution(Integer uid, Integer problemId, Integer contestId,
                    Integer language, Long submitTime, String sourceCode) {
        this.uid = uid;
        this.problemId = problemId;
        this.contestId = contestId;
        this.language = language;
        this.submitTime = submitTime;
        this.sourceCode = sourceCode;
        this.state = WAITING;
    }

    public void endWithError(Integer result, String info) {
        this.result = result;
        state = JUDGED;
        errorInfo = info;
    }

    public String getId() {
        return solutionId.toString();
    }

    public void setId(String id) {
        solutionId = new BigInteger(id);
    }
}

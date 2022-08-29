package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommitData {
    private String solutionId;
    private String userId;
    private Integer problemId;
    private Integer contestId;
    private String sourceCode;
    private Integer language;
    private Integer type;
    private Long submitTime;
}

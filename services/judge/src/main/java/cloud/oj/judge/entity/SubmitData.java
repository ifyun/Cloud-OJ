package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitData {
    private Integer uid;
    private Integer problemId;
    private Integer contestId;
    private String sourceCode;
    private Integer language;
    private Long submitTime;
}

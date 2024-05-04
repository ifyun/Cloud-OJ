package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

import static cloud.oj.core.entity.Solution.R;

@Getter
@Setter
public class Problem {
    private Integer contestId;
    private Boolean enable;
    private Integer problemId;
    private Integer result;
    private Integer passed;
    private Integer memoryLimit;
    private Integer outputLimit;
    private Integer languages;
    private Integer score;
    private Long timeout;
    // UNIX 时间戳(10 位)
    private Long startAt;
    private Long endAt;
    private Long createAt;
    private String contestName;
    private String title;
    private String description;
    private String category;
    private String resultText;

    @SuppressWarnings("unused")
    public void setResult(Integer result) {
        if (result == null) {
            return;
        }

        this.result = result;
        this.resultText = R[result];
    }
}

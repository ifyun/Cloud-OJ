package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Solution {
    private String solutionId;
    private Integer problemId;
    private String title;
    private String userId;
    private Integer passed;
    private Integer total;
    private Double passRate;
    private Double score;
    private Long time;
    private Long memory;
    private String errorInfo;
    private Integer language;
    private Integer state;
    private Integer result;
    private Long submitTime;

    @SuppressWarnings("unused")
    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime.getTime() / 1000;
    }
}

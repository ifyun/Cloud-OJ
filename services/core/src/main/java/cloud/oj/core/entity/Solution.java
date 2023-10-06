package cloud.oj.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("unused")
public class Solution {
    @JsonIgnore
    public static final String[] S = {"运行完成", "正在运行", "正在编译", "等待判题"};

    @JsonIgnore
    public static final String[] R = {
            "完全正确", "时间超限", "内存超限",
            "部分通过", "答案错误", "编译错误",
            "运行错误", "内部错误", "输出超限"
    };

    private Integer solutionId;
    private Integer problemId;
    private String title;
    private Integer uid;
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
    private String stateText;
    private String resultText;
    // UNIX 时间戳(13 位)
    private Long submitTime;

    public void setState(Integer state) {
        this.state = state;
        this.stateText = S[state];
    }

    public void setResult(Integer result) {
        this.result = result;
        this.resultText = R[result];
    }
}

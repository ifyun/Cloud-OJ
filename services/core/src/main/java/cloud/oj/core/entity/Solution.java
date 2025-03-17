package cloud.oj.core.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

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

    private BigInteger solutionId;
    private Integer problemId;
    private String title;
    private Integer uid;
    // username, nickname, realName 管理查询时存在
    private String username;
    private String nickname;
    private String realName;
    private Integer passed;
    private Integer total;
    private Double passRate;
    private Double score;
    private Long time;
    private Long memory;
    private Integer language;
    private Integer state;
    private Integer result;
    private String stateText;
    private String resultText;
    // UNIX 时间戳(13 位)
    private Long submitTime;
    private String errorInfo;
    private String sourceCode;

    @JsonGetter("solutionId")
    public String getId() {
        return solutionId == null ? null : solutionId.toString();
    }

    public void setState(Integer state) {
        this.state = state;
        this.stateText = S[state];
    }

    public void setResult(@Nullable Integer result) {
        this.result = result;

        if (result != null) {
            this.resultText = R[result];
        }
    }
}

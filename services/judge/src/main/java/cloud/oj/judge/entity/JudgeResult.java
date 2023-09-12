package cloud.oj.judge.entity;

import cloud.oj.judge.constant.Result;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 对应判题程序返回结果
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JudgeResult {
    private Integer code;
    private Integer result;
    private String error;
    private Integer total;
    private Integer passed;
    private Double passRate;
    private Long time;
    private Long memory;

    @JsonSetter("result")
    public void setResultStr(String result) {
        this.result = Result.ofString(result);
    }
}

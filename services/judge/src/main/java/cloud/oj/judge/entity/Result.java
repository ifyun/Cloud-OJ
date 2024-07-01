package cloud.oj.judge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 对应判题程序返回结果
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    public static final int CE = 6;  // 编译错误
    public static final int RE = 7;  // 运行错误
    public static final int IE = 8;  // 内部错误

    private Integer result;
    private Integer total;
    private Integer passed;
    private Double passRate;
    private Long time;
    private Long memory;
    private String error;
    private List<String> detail;

    public static Result withError(Integer result, String error) {
        var instance = new Result();
        instance.result = result;
        instance.error = error;
        return instance;
    }
}

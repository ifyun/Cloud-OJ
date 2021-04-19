package group._204.oj.judge.type;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 注意：判题程序和此枚举类型的数值不对应
 */
public enum SolutionResult implements BaseEnum {
    AC(1),  // 通过
    TLE(2), // 超时
    MLE(3), // 超内存
    PA(4),  // 部分通过
    WA(5),  // 答案错误
    CE(6),  // 编译错误
    RE(7),  // 运行错误
    IE(8),  // 内部错误
    OLE(9); // 输出超限

    private final int value;

    SolutionResult(int value) {
        this.value = value;
    }

    public static SolutionResult getByString(String str) {
        switch (str) {
            case "AC":
                return AC;
            case "WA":
                return WA;
            case "TLE":
                return TLE;
            case "MLE":
                return MLE;
            case "OLE":
                return OLE;
            case "PA":
                return PA;
            default:
                return IE;
        }
    }

    @Override
    public int getValue() {
        return value;
    }

    @JsonValue
    public int value() {
        return value - 1;
    }
}
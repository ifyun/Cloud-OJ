package group._204.oj.judge.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SolutionResult implements BaseEnum {
    PASSED(1),
    TIMEOUT(2),
    OOM(3),
    PARTLY_PASSED(4),
    WRONG(5),
    COMPILE_ERROR(6),
    RUNTIME_ERROR(7),
    JUDGE_ERROR(8);

    private final int value;

    SolutionResult(int value) {
        this.value = value;
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
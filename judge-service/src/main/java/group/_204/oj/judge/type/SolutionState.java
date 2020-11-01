package group._204.oj.judge.type;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 判题状态
 */
public enum SolutionState implements BaseEnum {
    JUDGED(1),
    IN_JUDGE_QUEUE(2);

    private final int value;

    SolutionState(int value) {
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

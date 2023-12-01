package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Problem {
    private int timeout;
    private int memoryLimit;
    private int outputLimit;
    private int score;
}

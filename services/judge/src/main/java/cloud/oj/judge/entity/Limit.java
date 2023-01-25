package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Limit {
    private long timeout;
    private int memoryLimit;
    private int outputLimit;
    private int score;
}

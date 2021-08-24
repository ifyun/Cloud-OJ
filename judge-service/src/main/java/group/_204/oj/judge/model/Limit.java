package group._204.oj.judge.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Limit {
    private long timeout;
    private int memoryLimit;
    private int score;
}

package cloud.oj.judge.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Limit {
    private long timeout;
    private int memoryLimit;
    private int outputLimit;
    private int score;
}

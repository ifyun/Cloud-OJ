package cloud.oj.judge.model;

import lombok.Data;

@Data
public class Contest {
    private String contestName;
    private boolean ended;
    private int languages;
}

package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Contest {
    private String contestName;
    private boolean started;
    private boolean ended;
    private int languages;
}

package cloud.oj.core.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contest {
    private Integer contestId;
    private String contestName;
    private Integer problemCount;
    private Long startAt;
    private Long endAt;
    private Integer languages;
    private boolean started;
    private boolean ended;
}

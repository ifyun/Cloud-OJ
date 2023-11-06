package cloud.oj.core.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contest {
    private Integer contestId;
    private String contestName;
    private String inviteKey;
    private Integer problemCount;
    // UNIX 时间戳(10 位)
    private Long startAt;
    private Long endAt;
    private Integer languages;
    private boolean started;
    private boolean ended;
    // UNIX 时间戳(10 位)
    private Long createAt;

    private List<Ranking> ranking;

    public Contest withoutKey() {
        this.inviteKey = null;
        return this;
    }
}

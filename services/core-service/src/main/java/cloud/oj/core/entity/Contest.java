package cloud.oj.core.entity;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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

    @JsonSetter
    public void setStartAtTimestamp(Long startAt) {
        this.startAt = startAt;
    }

    @JsonSetter
    public void setEndAtTimestamp(Long endAt) {
        this.endAt = endAt;
    }

    @SuppressWarnings("unused")
    public void setStartAt(Date startAt) {
        this.startAt = startAt.getTime() / 1000;
    }

    @SuppressWarnings("unused")
    public void setEndAt(Date endAt) {
        this.endAt = endAt.getTime() / 1000;
    }
}

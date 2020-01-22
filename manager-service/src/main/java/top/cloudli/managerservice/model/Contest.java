package top.cloudli.managerservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contest {
    private Integer contestId;
    private String contestName;
    private Date startAt;
    private Date endAt;

    public Contest(String contestName, Date startAt, Date endAt) {
        this(null, contestName, startAt, endAt);
    }
}

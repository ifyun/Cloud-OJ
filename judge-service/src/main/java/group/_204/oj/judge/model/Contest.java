package group._204.oj.judge.model;

import lombok.Data;

import java.util.Date;

@Data
public class Contest {
    private String contestName;
    private Date endAt;
    private int languages;
}

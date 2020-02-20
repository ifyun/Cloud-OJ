package top.cloudli.judgeservice.model;

import lombok.Data;

import java.util.Date;

@Data
public class Contest {
    private String contestName;
    private Date endAt;
}

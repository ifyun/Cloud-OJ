package top.cloudli.managerservice.model;

import lombok.Data;

import java.sql.Date;

@Data
public class Problem {
    private Integer contestId;
    private String contestName;
    private Date startAt;
    private Date endAt;
    private int problemId;
    private Integer result;
    private String title;
    private String description;
    private String input;
    private String output;
    private String sampleInput;
    private String sampleOutput;
    private String category;
    private long timeout;
    private int score;
    private boolean enable;
    private Date createAt;
}

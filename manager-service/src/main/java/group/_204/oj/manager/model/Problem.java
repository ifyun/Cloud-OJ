package group._204.oj.manager.model;

import lombok.Data;

import java.util.Date;

@Data
public class Problem {

    private Integer contestId;

    private String contestName;

    private Date startAt;

    private Date endAt;

    private Integer problemId;

    private Integer result;

    private Integer passed;

    private String title;

    private String description;

    private String category;

    private Long timeout;

    private Integer languages;

    private Integer score;
    private Boolean enable;
    private Date createAt;

    private TestData testData;
}

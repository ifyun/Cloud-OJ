package group._204.oj.manager.model;

import lombok.Data;

@Data
public class Ranking {
    private Integer rank;
    private String userId;
    private String name;
    private Integer committed;
    private Integer passed;
    private Double totalScore;
    private String contestName;
}

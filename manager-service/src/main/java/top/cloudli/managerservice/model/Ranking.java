package top.cloudli.managerservice.model;

import lombok.Data;

@Data
public class Ranking {
    private String userId;
    private String name;
    private int committed;
    private int passed;
    private double totalScore;
}

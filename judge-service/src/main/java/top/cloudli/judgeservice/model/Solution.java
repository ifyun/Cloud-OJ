package top.cloudli.judgeservice.model;

import lombok.Data;

@Data
public class Solution {
    private int solutionId;
    private int userId;
    private int state;
    private double passRate;
}

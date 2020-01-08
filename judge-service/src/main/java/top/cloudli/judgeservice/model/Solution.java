package top.cloudli.judgeservice.model;

import lombok.Data;

@Data
public class Solution {
    private int solutionId;
    private int problemId;
    private String userId;
    private int language;
    private int state;
    private double passRate;

    public Solution(String userId, int problemId, int language) {
        this.userId = userId;
        this.problemId = problemId;
        this.language = language;
    }
}

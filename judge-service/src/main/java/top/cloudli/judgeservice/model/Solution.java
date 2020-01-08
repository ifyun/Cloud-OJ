package top.cloudli.judgeservice.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Solution implements Serializable {
    private int solutionId;
    private int problemId;
    private int language;
    private String userId;
    private int state;
    private double passRate;

    public Solution(String userId, int problemId, int language) {
        this.userId = userId;
        this.problemId = problemId;
        this.language = language;
    }
}

package top.cloudli.judgeservice.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Solution implements Serializable {
    private Integer solutionId;
    private Integer problemId;
    private Integer contestId;
    private String userId;
    private Integer language;
    private Integer state;
    private Integer result;
    private double passRate;

    public Solution(String userId, Integer problemId, Integer contestId, Integer language) {
        this.userId = userId;
        this.problemId = problemId;
        this.contestId = contestId;
        this.language = language;
    }
}

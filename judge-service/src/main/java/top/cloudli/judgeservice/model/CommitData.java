package top.cloudli.judgeservice.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommitData implements Serializable {
    private String solutionId;
    private String userId;
    private Integer problemId;
    private Integer contestId;
    private String sourceCode;
    private Integer language;
}

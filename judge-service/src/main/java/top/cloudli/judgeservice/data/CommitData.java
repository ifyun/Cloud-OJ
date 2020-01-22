package top.cloudli.judgeservice.data;

import lombok.Data;

@Data
public class CommitData {
    private String userId;
    private Integer problemId;
    private Integer contestId;
    private String sourceCode;
    private Integer language;
}

package top.cloudli.judgeservice.data;

import lombok.Data;

@Data
public class CommitData {
    private String userId;
    private int problemId;
    private String sourceCode;
    private int language;
}

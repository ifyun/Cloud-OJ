package top.cloudli.judgeservice.model;

import lombok.Data;

@Data
public class SourceCode {
    private int codeId;
    private int solutionId;
    private String code;

    public SourceCode(int solutionId, String code) {
        this.solutionId = solutionId;
        this.code = code;
    }
}

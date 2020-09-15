package group._204.oj.judge.model;

import lombok.Data;

@Data
public class SourceCode {
    private Integer codeId;
    private String solutionId;
    private String code;

    public SourceCode(String solutionId, String code) {
        this.solutionId = solutionId;
        this.code = code;
    }
}

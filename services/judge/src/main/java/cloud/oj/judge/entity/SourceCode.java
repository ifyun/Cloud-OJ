package cloud.oj.judge.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceCode {
    private String solutionId;
    private String code;

    public SourceCode(String solutionId, String code) {
        this.solutionId = solutionId;
        this.code = code;
    }
}

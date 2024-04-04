package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
    private Integer AC;
    private Integer WA;
    private Integer CE;
    private Integer RE;
    private Integer MLE;
    private Integer TLE;
    private Integer total;
}

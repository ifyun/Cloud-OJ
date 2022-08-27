package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ranking {
    private Integer rank;
    private String userId;
    private String name;
    private Integer committed;
    private Integer passed;
    private Double score;
}

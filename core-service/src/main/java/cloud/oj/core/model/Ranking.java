package cloud.oj.core.model;

import lombok.Data;

@Data
public class Ranking {
    private Integer rank;
    private String userId;
    private String name;
    private Integer committed;
    private Integer passed;
    private Double score;
}

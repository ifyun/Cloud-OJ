package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ranking {
    private Integer rank;
    private Integer uid;
    private String username;
    private String nickname;
    private Integer committed;
    private Integer passed;
    private Double score;
    private Boolean hasAvatar;
}

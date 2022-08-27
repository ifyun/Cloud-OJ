package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Problem {
    private Integer contestId;
    private String contestName;
    private Date startAt;
    private Date endAt;
    private Integer problemId;
    private Integer result;
    private Integer passed;
    private String title;
    private String description;
    private String sql;
    private String category;
    private Long timeout;
    private Integer memoryLimit;
    private Integer outputLimit;
    private Integer languages;
    private Integer type;
    private Integer score;
    private Boolean enable;
    private Long createAt;

    @SuppressWarnings("unused")
    public void setCreateAt(Date createAt) {
        this.createAt = createAt.getTime() / 1000;
    }
}

package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户 AC 记录
 *
 * <p>按(problem_id，date)分组；按(problem_id，language)计数</p>
 */
@Getter
@Setter
public class AcCount {
    private Integer pid;
    private String date;
    private Integer count;
}

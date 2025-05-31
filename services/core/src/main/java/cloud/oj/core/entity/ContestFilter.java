package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 竞赛搜索条件
 */
@Getter
@Setter
public class ContestFilter {
    private String keyword = "";
    private Boolean hideEnded = false;
}

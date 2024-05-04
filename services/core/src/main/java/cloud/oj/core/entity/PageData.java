package cloud.oj.core.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 分页数据
 *
 * @param <T> 数据类型
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageData<T> {
    private List<T> data;       // 当前页的数据
    private Integer total;      // 总数
}

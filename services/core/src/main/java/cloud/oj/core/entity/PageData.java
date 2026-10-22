package cloud.oj.core.entity;

import java.util.List;

/**
 * 分页数据
 *
 * @param <T> 数据类型
 */
public record PageData<T>(
        List<T> data,      // 当前页的数据
        Integer total      // 总数
) {
}

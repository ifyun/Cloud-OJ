package cloud.oj.core.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedList {
    private List<?> data;
    private Integer count;

    /**
     * 解析 MyBatis 的分页数据
     *
     * @param data List of List
     * @return {@link PagedList}
     */
    public static PagedList resolve(List<List<?>> data) {
        return new PagedList(data.get(0), (Integer) data.get(1).get(0));
    }
}

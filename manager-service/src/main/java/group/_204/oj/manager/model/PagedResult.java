package group._204.oj.manager.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "分页数据")
public class PagedResult {
    @ApiModelProperty(value = "数据集合")
    private List<?> data;
    @ApiModelProperty(value = "数量")
    private Integer count;
}

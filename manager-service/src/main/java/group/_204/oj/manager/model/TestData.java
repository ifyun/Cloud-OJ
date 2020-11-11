package group._204.oj.manager.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@ApiModel(description = "测试数据，导入/导出题目时存在")
public class TestData {
    private List<String> input;
    private List<String> output;
}

package group._204.oj.fileserver.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "测试数据")
public class TestData {
    private String fileName;
    private String content;
    private long size;
}

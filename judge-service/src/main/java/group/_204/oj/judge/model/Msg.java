package group._204.oj.judge.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "消息提示")
public class Msg {
    private String msg;
}

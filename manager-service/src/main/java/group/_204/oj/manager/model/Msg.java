package group._204.oj.manager.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "消息提示")
public class Msg {
    @ApiModelProperty(hidden = true)
    private Integer status;
    private String msg;

    public Msg(int status) {
        this(status, null);
    }

    public Msg(String msg) {
        this.msg = msg;
    }
}

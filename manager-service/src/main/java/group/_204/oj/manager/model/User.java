package group._204.oj.manager.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "用户")
public class User {
    private String userId;
    private String name;
    @ApiModelProperty(hidden = true)
    private String password;
    private String email;
    private String section;
    private Integer roleId;
    private String roleName;
    private Date createAt;
}

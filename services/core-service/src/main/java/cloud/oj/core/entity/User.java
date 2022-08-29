package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    private String userId;
    private String name;
    private String password;
    private String secret;
    private String email;
    private String section;
    private Integer roleId;
    private String roleName;
    private Long createAt;

    @SuppressWarnings("unused")
    public void setCreateAt(Date createAt) {
        this.createAt = createAt.getTime() / 1000;
    }
}

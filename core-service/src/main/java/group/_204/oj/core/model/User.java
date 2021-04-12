package group._204.oj.core.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String userId;
    private String name;
    private String password;
    private String email;
    private String section;
    private Integer roleId;
    private String roleName;
    private Date createAt;
}

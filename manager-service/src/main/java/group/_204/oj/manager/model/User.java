package group._204.oj.manager.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String userId;
    private String name;
    private String password;
    private String email;
    private String section;
    private int roleId;
    private String roleName;
    private Date createAt;
}

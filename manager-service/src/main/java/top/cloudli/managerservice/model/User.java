package top.cloudli.managerservice.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

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

package top.cloudli.managerservice.model;

import lombok.Data;

@Data
public class User {
    private String userId;
    private String name;
    private String password;
    private String email;
    private String section;
    private int roleId = 3;
}

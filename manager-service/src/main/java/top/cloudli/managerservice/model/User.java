package top.cloudli.managerservice.model;

import lombok.Data;

@Data
public class User {
    private int userId;
    private String name;
    private String email;
    private String section;
}

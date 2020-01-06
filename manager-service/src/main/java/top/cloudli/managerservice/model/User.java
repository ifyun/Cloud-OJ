package top.cloudli.managerservice.model;

import lombok.Data;

@Data
public class User {
    private String userId;
    private String name;
    private String email;
    private String section;
}

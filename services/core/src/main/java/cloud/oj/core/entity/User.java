package cloud.oj.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    @JsonIgnore
    public Integer _total;

    private Integer uid;
    private String username;
    private String nickname;
    private String realName;
    private String password;
    private String secret;
    private String email;
    private String section;
    private Boolean hasAvatar;
    private Boolean star;
    private Integer role;
    // UNIX 时间戳(10位)
    private Long createAt;
}

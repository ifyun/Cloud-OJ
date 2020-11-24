package group._204.oj.gateway.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "用户信息")
public class User implements UserDetails {

    private String userId;
    private String name;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String secret;

    private String section;
    private String email;

    @ApiModelProperty("权限ID，该属性仅前端使用，实际权限的判断由 JWT Token 验证得出")
    private int roleId;
    private String roleName;

    @JsonIgnore
    private List<Role> roles;

    @ApiModelProperty("JWT Token")
    private String token;

    @ApiModelProperty("JWT Token 失效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expire;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return userId;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}

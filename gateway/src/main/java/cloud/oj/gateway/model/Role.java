package cloud.oj.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority {

    private int roleId;
    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }
}

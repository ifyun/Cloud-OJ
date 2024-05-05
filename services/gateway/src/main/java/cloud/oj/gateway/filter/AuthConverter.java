package cloud.oj.gateway.filter;

import cloud.oj.gateway.entity.UsernamePasswd;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * 认证转换器
 *
 * <p>将登录 POST 数据转换为 {@link UsernamePasswd}</p>
 */
public class AuthConverter implements AuthenticationConverter {

    private final ObjectMapper mapper;

    public AuthConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        var contentType = request.getHeader("Content-Type");

        try {
            if (contentType != null && contentType.contains("application/json")) {
                var o = mapper.readValue(request.getInputStream(), UsernamePasswd.class);
                return new UsernamePasswordAuthenticationToken(o.getUsername(), o.getPassword());
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}

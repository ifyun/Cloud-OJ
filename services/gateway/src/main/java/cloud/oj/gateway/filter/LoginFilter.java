package cloud.oj.gateway.filter;

import cloud.oj.gateway.error.GenericException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * 登录过滤器
 *
 * <p>用户登录成功后生成 JWT</p>
 * <p><b>基类已在 Spring 的 Filter 中注册，可直接作为 Bean 注入</b></p>
 */
@Component
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthConverter authConverter;

    private final HandlerExceptionResolver resolver;

    public LoginFilter(AuthenticationManager authManager, ObjectMapper mapper,
                       @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
        setAuthenticationManager(authManager);
        authConverter = new AuthConverter(mapper);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        var auth = authConverter.convert(request);

        if (auth == null) {
            var e = new GenericException(HttpStatus.BAD_REQUEST, "错误的认证数据");
            resolver.resolveException(request, response, null, e);
            return null;
        }

        return this.getAuthenticationManager().authenticate(auth);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) {
        var e = new GenericException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        resolver.resolveException(request, response, null, e);
    }
}

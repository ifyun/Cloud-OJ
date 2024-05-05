package cloud.oj.gateway.filter;

import cloud.oj.gateway.error.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * 登录过滤器
 * 用户登录成功后生成 JWT
 */
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private static ObjectMapper mapper;

    private final AuthConverter authConverter;

    /**
     * 登录失败处理器
     */
    static class FailureHandler implements AuthenticationFailureHandler {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException exception) throws IOException {
            log.error(exception.getMessage());
            var status = HttpStatus.UNAUTHORIZED;
            var error = new ErrorMessage(status, "用户名或密码错误");

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().write(mapper.writeValueAsBytes(error));
        }
    }

    public LoginFilter(AuthenticationManager auth, ObjectMapper mapper) {
        authConverter = new AuthConverter(mapper);
        LoginFilter.mapper = mapper;
        setAuthenticationManager(auth);
        setAuthenticationFailureHandler(new FailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        var auth = authConverter.convert(request);

        if (auth == null) {
            throw new UsernameNotFoundException("Bad authentication data");
        }

        return this.getAuthenticationManager().authenticate(authConverter.convert(request));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }
}

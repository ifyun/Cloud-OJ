package group._204.oj.gateway.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import group._204.oj.gateway.model.User;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;

/**
 * Jwt 登录过滤器
 * 用户通过验证后生成 token
 */
@Slf4j
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final int validTime;

    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public JwtLoginFilter(int validTime, String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl, "POST"));
        this.validTime = validTime;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // 读取请求中的用户名和密码
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info(String.format("User: %s, attempts login.", username));

        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException {
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();

        StringBuilder authoritiesString = new StringBuilder();

        for (GrantedAuthority authority : authorities)
            authoritiesString.append(authority.getAuthority()).append(",");

        User user = (User) authResult.getPrincipal();

        // 生成 jwt token
        Date expireAt = new Date(System.currentTimeMillis() + validTime * 3600000);
        String jwt = Jwts.builder()
                .claim("authorities", authoritiesString)
                .setSubject(authResult.getName())
                .setExpiration(expireAt)
                .signWith(SignatureAlgorithm.HS512, user.getSecret())
                .compact();

        user.setToken(jwt);
        user.setExpire(expireAt);

        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(user));
        writer.flush();
        writer.close();

        log.info("User: {}, login success.", user.getUserId());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(400);
    }
}

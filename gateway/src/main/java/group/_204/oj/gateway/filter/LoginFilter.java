package group._204.oj.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import group._204.oj.gateway.dao.UserDao;
import group._204.oj.gateway.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;

/**
 * 登录过滤器
 * 用户通过验证后生成 token
 */
@Slf4j
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${project.token-valid-time:3}")
    private int tokenValidTime;

    @Resource
    private UserDao userDao;

    @Resource
    private ObjectMapper objectMapper;

    public LoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl, "POST"));
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username != null && password != null) {
            log.info("User({}) attempts login.", username);

            return getAuthenticationManager()
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } else {
            throw new AuthenticationServiceException("username or password null.");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException {
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        StringBuilder authoritiesString = new StringBuilder();

        for (GrantedAuthority authority : authorities)
            authoritiesString.append(authority.getAuthority()).append(",");

        User user = (User) authResult.getPrincipal();
        String userId = user.getUserId();
        // 更新 Secret
        log.info("Update secret of user({}): {}", userId, userDao.updateSecret(userId) == 1);
        user.setSecret(userDao.getSecret(userId));

        // 生成 jwt token
        Date expireAt = new Date(System.currentTimeMillis() + tokenValidTime * 3600000L);
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

        log.info("Login success, userId={}.", user.getUserId());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.error("Authenticate Failed: {}", failed.getMessage());
        response.setStatus(400);
    }
}

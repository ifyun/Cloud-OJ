package group._204.oj.gateway.filter;

import group._204.oj.gateway.dao.UserDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Jwt token 验证过滤器
 */
@Slf4j
public class TokenVerifyFilter extends GenericFilterBean {

    @Resource
    private UserDao userDao;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        SecurityContextHolder.clearContext();

        String jwtToken = ((HttpServletRequest) request).getHeader("token");
        String userId = ((HttpServletRequest) request).getHeader("userId");

        if (jwtToken == null) {
            jwtToken = request.getParameter("token");
        }

        if (userId == null) {
            userId = request.getParameter("userId");
        }

        if (userId == null || jwtToken == null) {
            // 没有 token 时交给 Spring Security 去处理
            filterChain.doFilter(request, response);
        } else {
            String secret = userDao.getSecret(userId);
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(jwtToken)
                        .getBody();

                String username = claims.getSubject();

                List<GrantedAuthority> authorities = AuthorityUtils
                        .commaSeparatedStringToAuthorityList((String) claims.get("authorities"));

                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(token);

                filterChain.doFilter(request, response);
            } catch (JwtException | IllegalArgumentException e) {
                log.error(e.getMessage());
                ((HttpServletResponse) response).setStatus(401);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"msg\":\"" + e.getMessage() + "\"}");
            }
        }
    }
}
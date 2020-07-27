package top.cloudli.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import top.cloudli.gateway.dao.UserDao;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 由于 /** 被转发到前端，使用过滤器来实现 logoff.
 */
@Slf4j
public class LogoffFilter extends GenericFilterBean {

    private final UserDao userDao;

    public LogoffFilter(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // logoff 请求
        if (request.getServletPath().equals("/logoff") && request.getMethod().equals("DELETE")) {
            String userId = request.getParameter("userId");
            String token = request.getHeader("token");
            String secret = userDao.getSecret(userId);
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody();

                if (claims.getSubject().equals(userId)) {
                    userDao.updateSecret(userId);
                    SecurityContextHolder.clearContext();
                    log.info("{} 登出.", userId);
                    ((HttpServletResponse) servletResponse).setStatus(200);
                    servletResponse.setCharacterEncoding("utf-8");
                    servletResponse.setContentType("text/plain");
                    servletResponse.getWriter().write("已退出.");
                }
            } catch (JwtException e) {
                log.error(e.getMessage());
                ((HttpServletResponse) servletResponse).setStatus(400);
            }
        } else {
            // 非 logoff 请求
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}

package cloud.oj.gateway.filter;

import cloud.oj.gateway.error.GenericException;
import cloud.oj.gateway.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * JWT Token 验证过滤器
 */
@Slf4j
@Component
public class TokenVerifyFilter extends GenericFilterBean {

    private final UserService userService;

    private final HandlerExceptionResolver resolver;

    public TokenVerifyFilter(UserService userService,
                             @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.userService = userService;
        this.resolver = resolver;
    }

    /**
     * 验证 JWT
     *
     * <p>先后在请求头和 Query 中查找 JWT，若为空则跳过验证</p>
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        var httpRequest = new MutableRequest((HttpServletRequest) request);
        var httpResponse = (HttpServletResponse) response;

        var path = httpRequest.getServletPath();

        try {
            // 登录/登出跳过验证
            if (path.equals("/logoff") || path.equals("/login")) {
                chain.doFilter(request, response);
                return;
            }

            // 获取 Bearer Token
            var token = httpRequest.getHeader("Authorization");

            if (token != null) {
                // Authorization，去掉 'Bearer ' 前缀
                token = token.substring(7);
            } else {
                // Headers 不带 Token，在 URL 中查找
                token = httpRequest.getParameter("token");
            }

            if (token == null) {
                // 没有 Token，跳过验证
                chain.doFilter(request, response);
                return;
            }

            var uid = JwtUtil.getUid(token);

            if (uid == null) {
                throw new JwtException("Token 错误");
            }

            var secret = userService.getSecret(uid);

            if (secret.isEmpty()) {
                throw new ServletException("用户不存在");
            }

            var claims = JwtUtil.getClaims(token, secret.get());
            var authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));
            var auth = new UsernamePasswordAuthenticationToken(uid, null, authorities);

            // 为带 token 的请求加上 uid 请求头
            httpRequest.putHeader("uid", uid.toString());
            SecurityContextHolder.getContext().setAuthentication(auth);
            // 由 Spring 继续处理
            chain.doFilter(httpRequest, httpResponse);
        } catch (Exception e) {
            var status = HttpStatus.INTERNAL_SERVER_ERROR;
            var error = e.getMessage();

            if (e instanceof JwtException) {
                status = HttpStatus.UNAUTHORIZED;
                error = "无效的 Token";
            } else if (e instanceof ServletException) {
                resolver.resolveException(httpRequest, httpResponse, null, e);
            }

            resolver.resolveException(httpRequest, httpResponse, null, new GenericException(status, error));
        }
    }
}

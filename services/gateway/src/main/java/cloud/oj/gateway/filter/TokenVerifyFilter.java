package cloud.oj.gateway.filter;

import cloud.oj.gateway.error.ErrorMessage;
import cloud.oj.gateway.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.*;

/**
 * JWT Token 验证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenVerifyFilter extends GenericFilterBean {

    private final UserService userService;

    private final ObjectMapper objectMapper;

    /**
     * HttpServletRequest 包装，用于修改 Headers
     */
    private static class MutableRequest extends HttpServletRequestWrapper {

        private final Map<String, String> headers;

        public MutableRequest(HttpServletRequest request) {
            super(request);
            headers = new HashMap<>();
        }

        public void putHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            var value = headers.get(name);

            if (value != null) {
                return value;
            }

            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            var set = new HashSet<String>();

            var e = super.getHeaderNames();

            while (e.hasMoreElements()) {
                set.add(e.nextElement());
            }

            set.addAll(headers.keySet());

            return Collections.enumeration(set);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (headers.containsKey(name)) {
                return Collections.enumeration(Collections.singletonList(headers.get(name)));
            }

            return super.getHeaders(name);
        }
    }

    /**
     * 验证 JWT
     *
     * <p>先后在 Headers 和 Query 中查找 JWT，若为空则跳过验证</p>
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var httpRequest = new MutableRequest((HttpServletRequest) request);
        var httpResponse = (HttpServletResponse) response;

        var path = httpRequest.getServletPath();

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

        try {
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
            var error = e.getMessage();

            if (e instanceof JwtException) {
                error = "无效的 Token";
            }

            var status = HttpStatus.UNAUTHORIZED;
            var errorMessage = new ErrorMessage(status, error);

            httpResponse.setStatus(status.value());
            httpResponse.setHeader("Content-Type", "application/json");
            httpResponse.getWriter().write(objectMapper.writeValueAsString(errorMessage));
            httpResponse.getWriter().flush();
        }
    }
}

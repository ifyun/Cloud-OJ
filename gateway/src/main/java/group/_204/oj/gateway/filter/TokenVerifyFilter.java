package group._204.oj.gateway.filter;

import group._204.oj.gateway.dao.UserDao;
import group._204.oj.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

/**
 * JWT Token 验证过滤器
 */
@Slf4j
public class TokenVerifyFilter implements WebFilter {

    @Resource
    private UserDao userDao;

    /**
     * 验证 JWT
     * <p>先后在 Header 和 Query 中查找 JWT，若为空则跳过验证</p>
     */
    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // 登录登出直接跳过
        if (path.equals("/logoff") || path.equals("/login")) {
            return chain.filter(exchange);
        }

        String jwt = request.getHeaders().getFirst("token");

        if (jwt == null) {
            jwt = request.getQueryParams().getFirst("token");
        }

        if (jwt == null) {
            return chain.filter(exchange);
        }

        try {
            String userId = JwtUtil.getSubject(jwt);

            if (userId == null) {
                throw new JwtException("不正确的JWT");
            }

            log.info("Verify JWT: [Path: {}, User: {}]", request.getPath(), userId);

            String secret = userDao.getSecret(userId);
            Claims claims = JwtUtil.getClaims(jwt, secret);

            List<GrantedAuthority> authorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList((String) claims.get("authorities"));

            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            /* --------------- 添加权限到上下文 --------------- */
            return chain.filter(exchange).subscriberContext(ReactiveSecurityContextHolder.withAuthentication(token));
        } catch (JwtException | IllegalArgumentException e) {
            String error = e.getMessage();
            log.error("Verify JWT failed: {}", error);

            ServerHttpResponse response = exchange.getResponse();

            if (error.contains("expired")) {
                error = "失效的Token";
            } else if (error.contains("signature")) {
                error = "签名不匹配";
            }

            DataBuffer dataBuffer = response.bufferFactory().wrap(("{\"msg\":\"" + error + "\"}").getBytes());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json");

            return response.writeWith(Flux.just(dataBuffer));
        }
    }
}

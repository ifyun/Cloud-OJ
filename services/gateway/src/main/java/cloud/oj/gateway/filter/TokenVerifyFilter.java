package cloud.oj.gateway.filter;

import cloud.oj.gateway.error.ErrorMessage;
import cloud.oj.gateway.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * JWT Token 验证过滤器
 */
@Slf4j
public class TokenVerifyFilter implements WebFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 验证 JWT
     * <p>先后在 Header 和 Query 中查找 JWT，若为空则跳过验证</p>
     */
    @NonNull
    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        var request = exchange.getRequest();
        var path = request.getPath().toString();

        // 登录登出直接跳过
        if (path.equals("/logoff") || path.equals("/login")) {
            return chain.filter(exchange);
        }

        var token = request.getHeaders().getFirst("Authorization");

        if (token != null) {
            token = token.substring(6);
        } else {
            token = request.getQueryParams().getFirst("token");
        }

        if (token == null) {
            return chain.filter(exchange);
        }

        try {
            var uid = JwtUtil.getUid(token);

            if (uid == null) {
                throw new JwtException("错误的 Token");
            }

            log.debug("Verify token: [Path: {}, User: {}]", request.getPath(), uid);

            var secret = userService.getSecret(uid);
            var claims = JwtUtil.getClaims(token, secret);

            var authorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList((String) claims.get("authorities"));
            var auth = new UsernamePasswordAuthenticationToken(uid, null, authorities);
            // 为带 token 的请求加上 uid 请求头
            return chain.filter(
                    exchange.mutate().request(
                            request.mutate().header("uid", uid.toString()).build()
                    ).build()
            ).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        } catch (JwtException | IllegalArgumentException | StringIndexOutOfBoundsException e) {
            var error = e.getMessage();
            log.error("Verify token failed: {}", error);

            if (e instanceof StringIndexOutOfBoundsException) {
                error = "Token 错误";
            } else if (error.contains("expired")) {
                error = "Token 已过期";
            } else if (error.contains("signature")) {
                error = "签名不匹配";
            }

            var response = exchange.getResponse();
            var status = HttpStatus.UNAUTHORIZED;
            var errorMessage = new ErrorMessage(status, error);
            var dataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(errorMessage));

            response.setStatusCode(status);
            response.getHeaders().add("Content-Type", "application/json");

            return response.writeWith(Flux.just(dataBuffer));
        }
    }
}

package cloud.oj.gateway.filter;

import cloud.oj.gateway.dao.UserDao;
import cloud.oj.gateway.error.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

import javax.annotation.Resource;

/**
 * JWT Token 验证过滤器
 */
@Slf4j
public class TokenVerifyFilter implements WebFilter {

    @Resource
    private UserDao userDao;

    @Resource
    private ObjectMapper mapper;

    /**
     * 验证 JWT
     * <p>先后在 Header 和 Query 中查找 JWT，若为空则跳过验证</p>
     */

    @Override
    @NonNull
    @SneakyThrows
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        var request = exchange.getRequest();
        var path = request.getPath().toString();

        // 登录登出直接跳过
        if (path.equals("/logoff") || path.equals("/login")) {
            return chain.filter(exchange);
        }

        var token = request.getHeaders().getFirst("token");

        if (token == null) {
            token = request.getQueryParams().getFirst("token");
        }

        if (token == null) {
            return chain.filter(exchange);
        }

        try {
            var userId = JwtUtil.getSubject(token);

            if (userId == null) {
                throw new JwtException("错误的Token");
            }

            log.debug("Verify token: [Path: {}, User: {}]", request.getPath(), userId);

            var secret = userDao.getSecret(userId);
            var claims = JwtUtil.getClaims(token, secret);

            var authorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList((String) claims.get("authorities"));
            var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);

            return chain.filter(
                    exchange.mutate().request(
                            request.mutate().header("userId", userId).build()
                    ).build()
            ).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        } catch (JwtException | IllegalArgumentException | StringIndexOutOfBoundsException e) {
            var error = e.getMessage();
            log.error("Verify token failed: {}", error);

            if (e instanceof StringIndexOutOfBoundsException) {
                error = "错误的Token";
            } else if (error.contains("expired")) {
                error = "失效的Token";
            } else if (error.contains("signature")) {
                error = "签名不匹配";
            }

            var response = exchange.getResponse();
            var status = HttpStatus.UNAUTHORIZED;
            var errorMessage = new ErrorMessage(status, error);
            var dataBuffer = response.bufferFactory().wrap(mapper.writeValueAsBytes(errorMessage));

            response.setStatusCode(status);
            response.getHeaders().add("Content-Type", "application/json");

            return response.writeWith(Flux.just(dataBuffer));
        }
    }
}

package group._204.oj.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 登录过滤器
 * 用户登录成功后生成 JWT
 */
@Slf4j
public class LoginFilter extends AuthenticationWebFilter {

    /**
     * 登录失败处理器
     */
    static class FailureHandler implements ServerAuthenticationFailureHandler {

        @Override
        public Mono<Void> onAuthenticationFailure(WebFilterExchange exchange, AuthenticationException exception) {
            ServerHttpResponse response = exchange.getExchange().getResponse();
            DataBuffer dataBuffer = response.bufferFactory().wrap(("{\"msg\": \"用户名或密码错误\"}").getBytes());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json");

            return response.writeWith(Flux.just(dataBuffer));
        }
    }

    public LoginFilter(ReactiveAuthenticationManager authenticationManager,
                       ServerCodecConfigurer serverCodecConfigurer) {
        super(authenticationManager);
        setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login"));
        setServerAuthenticationConverter(new AuthenticationConverter(serverCodecConfigurer));
        setAuthenticationFailureHandler(new FailureHandler());
    }
}

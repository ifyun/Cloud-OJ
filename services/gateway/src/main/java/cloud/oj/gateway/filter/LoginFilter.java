package cloud.oj.gateway.filter;

import cloud.oj.gateway.error.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
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

    private static ObjectMapper mapper;

    /**
     * 登录失败处理器
     */
    static class FailureHandler implements ServerAuthenticationFailureHandler {
        @Override
        @SneakyThrows
        public Mono<Void> onAuthenticationFailure(WebFilterExchange exchange, AuthenticationException exception) {
            var response = exchange.getExchange().getResponse();
            var status = HttpStatus.UNAUTHORIZED;
            var error = new ErrorMessage(status, "用户名或密码错误");
            var dataBuffer = response.bufferFactory().wrap(mapper.writeValueAsBytes(error));

            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json");

            return response.writeWith(Flux.just(dataBuffer));
        }
    }

    public LoginFilter(ReactiveAuthenticationManager authenticationManager,
                       ServerCodecConfigurer serverCodecConfigurer,
                       ObjectMapper mapper) {
        super(authenticationManager);
        LoginFilter.mapper = mapper;
        setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login"));
        setServerAuthenticationConverter(new AuthenticationConverter(serverCodecConfigurer));
        setAuthenticationFailureHandler(new FailureHandler());
    }
}

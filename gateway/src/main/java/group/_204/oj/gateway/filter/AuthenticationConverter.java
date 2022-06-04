package group._204.oj.gateway.filter;

import group._204.oj.gateway.model.UsernamePasswd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * 认证转换器
 * <p>将登录 POST 数据转换为 {@link UsernamePasswd}</p>
 */
@Slf4j
public class AuthenticationConverter implements ServerAuthenticationConverter {

    private final ResolvableType usernamePasswdType = ResolvableType.forClass(UsernamePasswd.class);

    private final ServerCodecConfigurer serverCodecConfigurer;

    public AuthenticationConverter(ServerCodecConfigurer serverCodecConfigurer) {
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        MediaType contentType = request.getHeaders().getContentType();

        if (contentType != null && contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            return serverCodecConfigurer.getReaders().stream()
                    .filter(reader -> reader.canRead(this.usernamePasswdType, MediaType.APPLICATION_JSON))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No JSON reader"))
                    .readMono(this.usernamePasswdType, request, Collections.emptyMap())
                    .cast(UsernamePasswd.class)
                    .map(o -> new UsernamePasswordAuthenticationToken(o.getUsername(), o.getPassword()));
        } else {
            return Mono.empty();
        }
    }
}

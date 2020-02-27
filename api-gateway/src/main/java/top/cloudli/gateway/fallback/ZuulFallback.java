package top.cloudli.gateway.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class ZuulFallback implements FallbackProvider {
    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        log.error("到 {} 的路由无法转发.", route);
        return badGateway();
    }

    private ClientHttpResponse badGateway() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() {
                return HttpStatus.BAD_GATEWAY;
            }

            @Override
            public int getRawStatusCode() {
                return HttpStatus.BAD_GATEWAY.value();
            }

            @Override
            public String getStatusText() {
                return HttpStatus.BAD_GATEWAY.getReasonPhrase();
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
                return new ClassPathResource("static/error/502.html").getInputStream();
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_HTML);
                return headers;
            }
        };
    }
}

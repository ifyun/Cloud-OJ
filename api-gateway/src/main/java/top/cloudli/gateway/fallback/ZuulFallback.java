package top.cloudli.gateway.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
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
        String msg = cause.getCause().getMessage();
        log.error("到 {} 的路由无法转发，{}.", route, msg);
        if (msg.contains("Read timed out"))
            return errorPage(HttpStatus.GATEWAY_TIMEOUT);
        else
            return errorPage(HttpStatus.BAD_GATEWAY);
    }

    private ClientHttpResponse errorPage(HttpStatus status) {
        return new ClientHttpResponse() {

            @NonNull
            @Override
            public HttpStatus getStatusCode() {
                return status;
            }

            @Override
            public int getRawStatusCode() {
                return status.value();
            }

            @NonNull
            @Override
            public String getStatusText() {
                return status.getReasonPhrase();
            }

            @Override
            public void close() {
            }

            @NonNull
            @Override
            public InputStream getBody() throws IOException {
                return new ClassPathResource("static/error/" + status.value() + ".html").getInputStream();
            }

            @NonNull
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_HTML);
                return headers;
            }
        };
    }
}

package cloud.oj.gateway.error;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalErrorHandler {

    private final ObjectMapper mapper;

    @ExceptionHandler(RuntimeException.class)
    public void errorHandler(HttpServletResponse response, RuntimeException e)
            throws IOException {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var msg = ExceptionUtils.getRootCause(e).getMessage();

        log.error(msg);

        if (e instanceof HttpServerErrorException) {
            // eg: 503 Service Unavailable
            status = (HttpStatus) ((HttpServerErrorException) e).getStatusCode();
            msg = status.getReasonPhrase();
        } else if (e instanceof GenericException) {
            status = ((GenericException) e).getStatus();
        }

        response.setCharacterEncoding("UTF-8");

        var writer = response.getWriter();
        var body = mapper.writeValueAsString(new ErrorMessage(status, msg));

        response.setStatus(status.value());
        response.setContentType("application/json");

        writer.print(body);
        writer.flush();
        writer.close();
    }
}

package cloud.oj.core.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalErrorHandler {

    private final ObjectMapper mapper;

    @ExceptionHandler(RuntimeException.class)
    public void otherErrorHandler(HttpServletRequest request, HttpServletResponse response, RuntimeException e)
            throws IOException {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var msg = ExceptionUtils.getRootCause(e).getMessage();

        log.error(msg);

        if (e instanceof GenericException) {
            status = ((GenericException) e).getStatus();
        }

        response.setCharacterEncoding("UTF-8");

        var writer = response.getWriter();
        var accept = request.getHeader("Accept");

        if (accept != null && accept.contains("text/event-stream")) {
            // 请求类型为 EventSource，发送一个 err 事件
            response.setStatus(200);
            response.setContentType("text/event-stream");
            writer.print("event: err\n");
            writer.print("data: " + msg + "\n\n");
        } else {
            var body = mapper.writeValueAsString(new ErrorMessage(status, msg));
            response.setStatus(status.value());
            response.setContentType("application/json");
            writer.print(body);
        }

        writer.flush();
        writer.close();
    }
}

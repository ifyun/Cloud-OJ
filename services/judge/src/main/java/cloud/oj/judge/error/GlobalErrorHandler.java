package cloud.oj.judge.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

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

        if (e instanceof AsyncRequestTimeoutException) {
            msg = ((AsyncRequestTimeoutException) e).getBody().getDetail();
        }

        log.error(msg);

        if (response.isCommitted()) {
            return;
        }

        if (e instanceof GenericException) {
            status = ((GenericException) e).getStatus();
        }

        // RabbitMQ 队列拒绝提交
        if (msg != null && msg.contains("nack")) {
            status = HttpStatus.NOT_ACCEPTABLE;
            msg = "队列已满";
        }

        response.setCharacterEncoding("UTF-8");

        var writer = response.getWriter();
        var accept = request.getHeader("Accept");

        if (accept != null && accept.contains("text/event-stream")) {
            // 请求类型为 EventSource，发送一个 error 事件
            response.setStatus(status.value());
            response.setContentType("text/event-stream");
            writer.print("event: error\n");
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

package cloud.oj.core.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalErrorHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> otherErrorHandler(RuntimeException e) {
        HttpStatus status;
        var msg = ExceptionUtils.getRootCause(e).getMessage();
        log.error(msg);

        if (e instanceof GenericException) {
            status = ((GenericException) e).getStatus();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(status).body(new ErrorMessage(status, msg));
    }
}

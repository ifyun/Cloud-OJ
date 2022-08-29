package cloud.oj.core.error;

import lombok.extern.slf4j.Slf4j;
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
        log.error(e.getMessage());
        var msg = new ErrorMessage(e);
        return ResponseEntity.status(msg.getStatus()).body(msg);
    }
}

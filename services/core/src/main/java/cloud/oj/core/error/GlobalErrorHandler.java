package cloud.oj.core.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalErrorHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handle0(RuntimeException e) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var msg = e.getMessage();
        log.error(msg);

        return ResponseEntity.status(status)
                .body(new ErrorMessage(status, msg));
    }

    public ResponseEntity<?> handler1(SQLException e) {
        log.error(e.getMessage());
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var msg = "SQL " + e.getErrorCode();

        return ResponseEntity.status(status)
                .body(new ErrorMessage(status, msg));
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<?> handle2(GenericException e)
            throws IOException {
        var status = e.getStatus();
        var msg = e.getMessage();
        log.error(msg);

        return ResponseEntity.status(status)
                .body(new ErrorMessage(status, msg));
    }
}

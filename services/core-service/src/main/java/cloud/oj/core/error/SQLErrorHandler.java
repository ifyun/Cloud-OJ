package cloud.oj.core.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * SQL 异常处理
 */
@Slf4j
@Order(1)
@RestControllerAdvice
public class SQLErrorHandler {

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> sqlErrorHandler(SQLException e) {
        var code = e.getErrorCode();
        log.error("code: {}, message: {}", code, e.getMessage());

        HttpStatus status;

        if (code == 1062 || code == 1451) {
            status = HttpStatus.CONFLICT;
        } else if (code == 1048) {
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        var msg = new ErrorMessage(status, e.getMessage());

        return ResponseEntity.status(status).body(msg);
    }
}

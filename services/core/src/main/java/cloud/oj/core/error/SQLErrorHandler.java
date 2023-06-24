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
        log.error("数据库错误: code={}, msg={}", e.getErrorCode(), e.getMessage());

        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var msg = new ErrorMessage(status, "数据库异常");

        return ResponseEntity.status(status).body(msg);
    }
}

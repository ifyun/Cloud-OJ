package top.cloudli.managerservice.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<String> sqlErrorHandler(SQLIntegrityConstraintViolationException e) {
        int code = e.getErrorCode();
        log.error(String.valueOf(e.getMessage()));

        String info = null;

        if (code == 1062) {
            info = "主键冲突.";
        } else if (code == 1048) {
            info = "数据不完整.";
        }

        return ResponseEntity.status(400).body(info);
    }
}

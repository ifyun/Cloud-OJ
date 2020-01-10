package top.cloudli.managerservice.error;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({SQLException.class, CommunicationsException.class})
    public ResponseEntity<String> sqlErrorHandler(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(500).body(e.getMessage());
    }
}

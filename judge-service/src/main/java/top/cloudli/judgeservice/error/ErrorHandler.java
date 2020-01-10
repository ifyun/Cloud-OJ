package top.cloudli.judgeservice.error;

import com.netflix.discovery.shared.transport.TransportException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({DataIntegrityViolationException.class, TransportException.class})
    public void sqlErrorHandler(SQLException e) {
        log.error(e.getMessage());
    }
}

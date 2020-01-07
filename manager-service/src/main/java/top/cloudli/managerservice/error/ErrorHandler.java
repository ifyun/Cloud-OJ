package top.cloudli.managerservice.error;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.cloudli.managerservice.data.Result;

import java.sql.SQLException;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({SQLException.class, CommunicationsException.class})
    public Result sqlErrorHandler(Exception e) {
        log.error(e.getMessage());
        return new Result(500, e.getMessage(), null);
    }
}

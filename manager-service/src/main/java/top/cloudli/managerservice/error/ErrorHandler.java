package top.cloudli.managerservice.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.cloudli.managerservice.model.Result;

import java.sql.SQLException;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(SQLException.class)
    public Result sqlErrorHandler(SQLException e) {
        log.error(e.getMessage());
        return new Result(500, e.getMessage(), null);
    }

}

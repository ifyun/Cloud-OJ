package top.cloudli.loadbalanced.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handler(Exception e) throws Exception {
        String error = e.getMessage();
        log.warn(error);

        if (error.contains("500"))
            throw new Exception("基础服务异常.");
        else
            return ResponseEntity
                    .status(Integer.parseInt(error.substring(0, error.indexOf(":")).trim()))
                    .build();
    }
}

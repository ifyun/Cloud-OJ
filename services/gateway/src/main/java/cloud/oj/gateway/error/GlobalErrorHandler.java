package cloud.oj.gateway.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

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

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<?> handle1(HttpServerErrorException e) {
        var status = (HttpStatus) e.getStatusCode();
        var msg = status.getReasonPhrase();
        log.error(e.getMessage());

        return ResponseEntity.status(status)
                .body(new ErrorMessage(status, msg));
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<?> handle2(GenericException e) {
        var status = e.getStatus();
        var msg = e.getMessage();
        log.error(msg);

        return ResponseEntity.status(status)
                .body(new ErrorMessage(status, msg));
    }
}

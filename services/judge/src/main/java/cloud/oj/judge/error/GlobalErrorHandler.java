package cloud.oj.judge.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.AmqpNackReceivedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<?> handle1(GenericException e) {
        var status = e.getStatus();
        var msg = e.getMessage();
        log.error(msg);

        return ResponseEntity.status(status)
                .body(new ErrorMessage(status, msg));
    }

    @ExceptionHandler(AmqpNackReceivedException.class)
    public ResponseEntity<?> handle2(AmqpNackReceivedException e) {
        log.error(e.getMessage());
        var status = HttpStatus.NOT_ACCEPTABLE;

        return ResponseEntity.status(status)
                .body(new ErrorMessage(status, "队列已满"));
    }
}

package cloud.oj.judge.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GenericException extends RuntimeException {
    private final HttpStatus status;

    public GenericException(HttpStatus status, String msg) {
        super(msg);
        this.status = status;
    }
}

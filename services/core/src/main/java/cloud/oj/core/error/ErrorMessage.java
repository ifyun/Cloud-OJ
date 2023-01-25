package cloud.oj.core.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorMessage {
    private Long timestamp;
    private Integer status;
    private String error;
    private String message;

    public ErrorMessage() {
        timestamp = System.currentTimeMillis() / 1000;
    }

    public ErrorMessage(RuntimeException e) {
        this();

        if (e instanceof GenericException) {
            status = ((GenericException) e).getStatus();
        } else {
            status = 500;
        }

        timestamp = System.currentTimeMillis() / 1000;
        error = HttpStatus.valueOf(status).getReasonPhrase();
        message = e.getMessage();
    }

    public ErrorMessage(HttpStatus status, String message) {
        this();
        this.status = status.value();
        this.message = message;
    }
}

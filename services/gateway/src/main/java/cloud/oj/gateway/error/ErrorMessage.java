package cloud.oj.gateway.error;

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

    public ErrorMessage(HttpStatus status, String message) {
        this();
        this.status = status.value();
        this.message = message;
        error = status.getReasonPhrase();
    }
}

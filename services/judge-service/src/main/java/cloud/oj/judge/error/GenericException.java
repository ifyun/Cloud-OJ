package cloud.oj.judge.error;

import lombok.Getter;

@Getter
public class GenericException extends RuntimeException {
    private final Integer status;

    public GenericException(Integer status, String msg) {
        super(msg);
        this.status = status;
    }
}

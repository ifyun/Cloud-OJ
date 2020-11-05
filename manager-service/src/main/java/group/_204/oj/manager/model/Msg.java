package group._204.oj.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Msg {
    private Integer status;
    private String msg;

    public Msg(int status) {
        this(status, null);
    }

    public Msg(String msg) {
        this.msg = msg;
    }
}

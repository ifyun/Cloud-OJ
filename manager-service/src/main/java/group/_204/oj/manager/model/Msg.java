package group._204.oj.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Msg {
    private int status;
    private String text;

    public Msg(int status) {
        this(status, null);
    }
}

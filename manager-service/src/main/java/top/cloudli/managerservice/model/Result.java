package top.cloudli.managerservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 最终返回的数据实体类
 */
@Data
@AllArgsConstructor
public class Result {
    private int code;
    private String info;
    private Object data;

    public Result(Object data) {
        this(200, "success", data);
    }
}

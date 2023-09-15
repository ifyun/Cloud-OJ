package cloud.oj.judge.constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 对应 solution 表中的 state 字段
 * <p>MariaDB 的枚举值从 1 开始</p>
 */
@Retention(RetentionPolicy.SOURCE)
public @interface State {
    int JUDGED = 1;
    int RUNNING = 2;
    int COMPILING = 3;
    int WAITING = 4;
}

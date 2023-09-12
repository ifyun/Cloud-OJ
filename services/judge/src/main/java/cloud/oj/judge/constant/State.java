package cloud.oj.judge.constant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对应 solution 表中的 state 字段
 * <p>MariaDB 的枚举值从 1 开始</p>
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface State {
    int JUDGED = 1;
    int IN_QUEUE = 2;
}

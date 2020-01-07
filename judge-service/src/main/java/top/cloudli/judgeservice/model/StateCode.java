package top.cloudli.judgeservice.model;

/**
 * 判题状态
 */
public enum StateCode {
    JUDGED,
    IN_JUDGE_QUEUE,
    COMPILED,
    IN_COMPILE_QUEUE,
    COMMITTED
}

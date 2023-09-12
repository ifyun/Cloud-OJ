package cloud.oj.judge.constant;

/**
 * 对应 solution 表中的 result 字段
 * <p>MariaDB 的枚举值从 1 开始</p>
 */
public class Result {
    public static final int AC = 1;  // 通过
    public static final int TLE = 2; // 超时
    public static final int MLE = 3; // 超内存
    public static final int PA = 4;  // 部分通过
    public static final int WA = 5;  // 答案错误
    public static final int CE = 6;  // 编译错误
    public static final int RE = 7;  // 运行错误
    public static final int IE = 8;  // 内部错误
    public static final int OLE = 9; // 输出超限

    public static Integer ofString(String str) {
        return switch (str) {
            case "AC" -> AC;
            case "TLE" -> TLE;
            case "MLE" -> MLE;
            case "PA" -> PA;
            case "WA" -> WA;
            case "CE" -> CE;
            case "RE" -> RE;
            case "IE" -> IE;
            case "OLE" -> OLE;
            default -> null;
        };
    }
}

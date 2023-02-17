package cloud.oj.judge.component;

public class SolutionResult {
    public static final int AC = 1;  // 通过
    public static final int TLE = 2; // 超时
    public static final int MLE = 3; // 超内存
    public static final int PA = 4;  // 部分通过
    public static final int WA = 5;  // 答案错误
    public static final int CE = 6;  // 编译错误
    public static final int RE = 7;  // 运行错误
    public static final int IE = 8;  // 内部错误
    public static final int OLE = 9; // 输出超限

    public static Integer fromString(String str) {
        return switch (str) {
            case "AC" -> AC;
            case "WA" -> WA;
            case "TLE" -> TLE;
            case "MLE" -> MLE;
            case "OLE" -> OLE;
            case "PA" -> PA;
            case "IE" -> IE;
            default -> null;
        };
    }
}

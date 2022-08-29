package cloud.oj.judge.error;

public class UnsupportedLanguageError extends RuntimeException {
    public UnsupportedLanguageError(String msg) {
        super("不支持的语言: " + msg);
    }
}

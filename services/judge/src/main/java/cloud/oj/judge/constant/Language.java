package cloud.oj.judge.constant;

import cloud.oj.judge.error.UnsupportedLanguageError;

public class Language {
    public static final int C = 0;
    public static final int CPP = 1;
    public static final int JAVA = 2;
    public static final int PYTHON = 3;
    public static final int BASH = 4;
    public static final int C_SHARP = 5;
    public static final int JAVA_SCRIPT = 6;
    public static final int KOTLIN = 7;
    public static final int GO = 8;

    private static final String[] extensions = {".c", ".cpp", ".java", ".py", ".sh", ".cs", ".js", ".kt", ".go"};

    public static void check(Integer code) throws UnsupportedLanguageError {
        if (code == null || code < 0 || code > 8) {
            throw new UnsupportedLanguageError(String.valueOf(code));
        }
    }

    public static String getExt(int code) {
        return extensions[code];
    }
}

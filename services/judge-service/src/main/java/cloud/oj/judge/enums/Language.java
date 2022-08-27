package cloud.oj.judge.enums;

public enum Language {
    C(0, ".c"),
    CPP(1, ".cpp"),
    JAVA(2, ".java"),
    PYTHON(3, ".py"),
    BASH(4, ".sh"),
    C_SHARP(5, ".cs"),
    JAVA_SCRIPT(6, ".js"),
    KOTLIN(7, ".kt"),
    GO(8, ".go");

    private final int code;
    private final String ext;

    Language(int code, String ext) {
        this.code = code;
        this.ext = ext;
    }

    public static Language get(int code) {
        for (Language language : Language.values()) {
            if (language.code == code) {
                return language;
            }
        }

        return null;
    }

    public static String getExt(int code) {
        for (Language language : values()) {
            if (language.code == code) {
                return language.ext;
            }
        }

        return null;
    }
}

package top.cloudli.judgeservice.model;

public enum Language {
    C_CPP(0, ".cpp"),
    JAVA(1, ".java"),
    PYTHON(2, ".py"),
    BASH(3, ".sh"),
    C_SHARP(4, ".cs");

    private int code;
    private String ext;

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

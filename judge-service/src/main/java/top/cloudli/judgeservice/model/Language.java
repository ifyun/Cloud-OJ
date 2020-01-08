package top.cloudli.judgeservice.model;

public enum Language {
    C_CPP(0, ".cpp"),
    JAVA(1, ".java"),
    PYTHON(2, ".py"),
    BASH(3, ".sh");

    private int code;
    private String value;

    Language(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static Language get(int code) {
        for (Language language : values()) {
            return language.code == code ? language : null;
        }
        return null;
    }

    public static String getExt(int code) {
        for (Language language : values()) {
            return language.code == code ? language.value : null;
        }
        return null;
    }
}

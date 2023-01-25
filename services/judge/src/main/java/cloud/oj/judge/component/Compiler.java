package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.error.UnsupportedLanguageError;
import cloud.oj.judge.entity.Compile;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.enums.Language;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
class Compiler {

    @Resource
    private AppConfig appConfig;

    private final ProcessBuilder processBuilder = new ProcessBuilder();

    private static class CompileError extends Exception {
        CompileError(String msg) {
            super(msg);
        }
    }

    /**
     * 编译
     */
    public Compile compile(Solution solution) {
        var solutionId = solution.getSolutionId();
        var languageId = solution.getLanguage();
        var sourceCode = solution.getSourceCode();

        var src = writeCode(solutionId, languageId, sourceCode);

        if (src.isEmpty()) {
            var err = "编译错误: 无法写入代码.";
            log.error(err);
            return new Compile(solutionId, -1, err);
        }

        try {
            var language = Language.get(languageId);
            return compileSource(solutionId, language);
        } catch (UnsupportedLanguageError e) {
            log.error("编译错误: {}", e.getMessage());
            return new Compile(solutionId, -1, e.getMessage());
        }
    }

    /**
     * 根据语言类型编译源码
     *
     * @return {@link Compile} 编译结果
     */
    private Compile compileSource(String solutionId, Language language) throws UnsupportedLanguageError {
        if (language == null) {
            throw new UnsupportedLanguageError("NULL");
        }

        var solutionDir = appConfig.getCodeDir() + solutionId;
        String[] cmd;

        switch (language) {
            case C:
                cmd = new String[]{"gcc", "-std=c11", "-fmax-errors=3", "-Wfatal-errors", "-lm",
                        "Solution.c", "-o", "Solution"};
                break;
            case CPP:
                cmd = new String[]{"g++", "-std=c++17", "-fmax-errors=3", "-Wfatal-errors", "-lm",
                        "Solution.cpp", "-o", "Solution"};
                break;
            case JAVA:
                cmd = new String[]{"javac", "-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "Solution.java"};
                break;
            case C_SHARP:
                cmd = new String[]{"mcs", "Solution.cs"};
                break;
            case KOTLIN:
                cmd = new String[]{"kotlinc", "Solution.kt"};
                break;
            case GO:
                cmd = new String[]{"go", "build", "Solution.go"};
                break;
            case PYTHON:
            case BASH:
            case JAVA_SCRIPT:
                return new Compile(solutionId, 0);
            default:
                throw new UnsupportedLanguageError(language.toString());
        }

        try {
            processBuilder.command(cmd);
            processBuilder.directory(new File(solutionDir));

            var process = processBuilder.start();

            if (process.waitFor(20, TimeUnit.SECONDS)) {
                if (process.exitValue() == 0) {
                    return new Compile(solutionId, 0, null);
                } else {
                    String error = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
                    throw new CompileError(error);
                }
            } else {
                process.destroy();
                throw new InterruptedException("编译超时(10s).");
            }
        } catch (IOException | InterruptedException e) {
            log.error("Compile error: solutionId={}, error={}", solutionId, e.getMessage());
            return new Compile(solutionId, -1, "内部错误.");
        } catch (CompileError e) {
            log.error("Compile error: solutionId={}, error={}", solutionId, e.getMessage());
            return new Compile(solutionId, -1, e.getMessage());
        }
    }

    /**
     * 将源码写入文件
     *
     * @return 文件路径
     */
    private String writeCode(String solutionId, int language, String source) {
        var solutionDir = new File(appConfig.getCodeDir() + solutionId);

        if (!solutionDir.mkdirs()) {
            log.error("Cannot create dir {}.", solutionDir.getName());
            return "";
        }

        var sourceFile = new File(solutionDir + "/Solution" + Language.getExt(language));

        try {
            if (sourceFile.exists() || sourceFile.createNewFile()) {
                var writer = new FileWriter(sourceFile, false);
                writer.write(source);
                writer.flush();
                writer.close();

                return sourceFile.getPath();
            } else {
                log.error("Cannot creat file {}.", sourceFile.getName());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return "";
    }
}

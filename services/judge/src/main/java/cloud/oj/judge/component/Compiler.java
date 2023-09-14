package cloud.oj.judge.component;

import cloud.oj.judge.config.AppConfig;
import cloud.oj.judge.constant.Language;
import cloud.oj.judge.entity.Compile;
import cloud.oj.judge.entity.Solution;
import cloud.oj.judge.error.UnsupportedLanguageError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Compiler {

    private static final Map<Integer, String[]> CMD = Map.of(
            Language.C, new String[]{"gcc", "-std=c11", "-fmax-errors=3", "-Wfatal-errors", "-lm", "Solution.c", "-o", "Solution"},
            Language.CPP, new String[]{"g++", "-std=c++17", "-fmax-errors=3", "-Wfatal-errors", "-lm", "Solution.cpp", "-o", "Solution"},
            Language.JAVA, new String[]{"javac", "-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "Solution.java"},
            Language.C_SHARP, new String[]{"mcs", "Solution.cs"},
            Language.KOTLIN, new String[]{"kotlinc-native", "Solution.kt", "-o", "Solution"},
            Language.GO, new String[]{"go", "build", "Solution.go"}
    );

    private final AppConfig appConfig;

    private final ProcessBuilder processBuilder = new ProcessBuilder();

    public Compiler(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

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
        var language = solution.getLanguage();
        var sourceCode = solution.getSourceCode();

        try {
            Language.check(language);
            var src = writeCode(solutionId, language, sourceCode);

            if (src.isEmpty()) {
                var err = "编译错误: 无法写入代码.";
                log.error(err);
                return new Compile(solutionId, -1, err);
            }

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
    private Compile compileSource(Integer solutionId, Integer language) {
        var solutionDir = appConfig.getCodeDir() + solutionId;

        try {
            if (language == Language.PYTHON || language == Language.BASH || language == Language.JAVA_SCRIPT) {
                return new Compile(solutionId, 0);
            }

            processBuilder.command(CMD.get(language));
            processBuilder.directory(new File(solutionDir));

            var process = processBuilder.start();

            if (process.waitFor(60, TimeUnit.SECONDS)) {
                if (process.exitValue() == 0) {
                    return new Compile(solutionId, 0, null);
                } else {
                    String error = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
                    throw new CompileError(error);
                }
            } else {
                process.destroy();
                throw new InterruptedException("编译超时");
            }
        } catch (IOException e) {
            log.error("编译错误({}): {}", solutionId, e.getMessage());
            return new Compile(solutionId, -1, "编译器可能不存在");
        } catch (InterruptedException | CompileError e) {
            log.error("编译错误({}): {}", solutionId, e.getMessage());
            return new Compile(solutionId, -1, e.getMessage());
        }
    }

    /**
     * 将源码写入文件
     *
     * @return 文件路径
     */
    private String writeCode(Integer solutionId, int language, String source) {
        var solutionDir = new File(appConfig.getCodeDir() + solutionId);

        if (!solutionDir.mkdirs()) {
            log.error("创建目录失败: {}", solutionDir.getName());
            return "";
        }

        var sourceFile = new File(solutionDir + "/Solution" + Language.getExt(language));

        try {
            //noinspection ResultOfMethodCallIgnored
            sourceFile.createNewFile();
            var writer = new FileWriter(sourceFile, false);
            writer.write(source);
            writer.flush();
            writer.close();

            return sourceFile.getPath();
        } catch (IOException e) {
            log.error("创建文件失败({}): {}", sourceFile.getName(), e.getMessage());
            return "";
        }
    }
}

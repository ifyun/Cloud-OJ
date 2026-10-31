package cloud.oj.judge.config;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Configuration;

import cloud.oj.judge.entity.Env;
import cloud.oj.judge.repo.EnvRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RuntimeEnv {

    private final ProcessBuilder pb = new ProcessBuilder();

    private final EnvRepo envRepo;

    @SneakyThrows
    @PostConstruct
    public void read() {
        var env = List.of(
                new Env(0, "C", cmd("gcc", "--version")),
                new Env(1, "C++", cmd("g++", "--version")),
                new Env(2, "Java", cmd("java", "--version")),
                new Env(3, "Python", cmd("python3", "--version")),
                new Env(4, "Bash", cmd("bash", "--version")),
                new Env(5, "C#", "dotnet-sdk-" + cmd("dotnet", "--version")),
                new Env(6, "JavaScript", cmd("qjs", "--help")),
                new Env(7, "Kotlin", cmd("kotlinc", "-version").replaceFirst("^info: ", "")),
                new Env(8, "Go", cmd("go", "version").replaceFirst("^go version ", "")));
        envRepo.update(env);
    }

    private String cmd(String... cmd) {
        pb.command(cmd);

        try {
            var p = pb.start();
            p.waitFor();

            var stdout = IOUtils.readLines(p.getInputStream(), "UTF-8");
            var stderr = IOUtils.readLines(p.getErrorStream(), "UTF-8");

            if (!stdout.isEmpty()) {
                return stdout.getFirst();
            }

            if (!stderr.isEmpty()) {
                return stderr.getFirst();
            }

            return "-";
        } catch (Exception e) {
            log.error(e.getMessage());
            return "error";
        }
    }
}

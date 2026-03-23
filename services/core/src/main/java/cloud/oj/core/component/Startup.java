package cloud.oj.core.component;

import cloud.oj.core.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class Startup implements CommandLineRunner {

    private final ApplicationContext context;

    private final AppConfig appConfig;

    private final JdbcTemplate jdbc;

    @Override
    public void run(String... args) {
        AvailabilityChangeEvent.publish(context, LivenessState.BROKEN);
        cleanImages();
        AvailabilityChangeEvent.publish(context, LivenessState.CORRECT);
    }

    private void cleanImages() {
        final var IMG_PATTERN = Pattern.compile("!\\[[^]]*]\\((?!https?://)([^\\s)]+)(?:\\s+\"[^\"]*\")?\\)");
        final Set<String> refs = ConcurrentHashMap.newKeySet();

        jdbc.query((con) -> {
                    var sql = "select description from problem";
                    var stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                    stmt.setFetchSize(50);
                    return stmt;
                }, rs -> {
                    var md = rs.getString("description");
                    IMG_PATTERN.matcher(md)
                            .results()
                            .map(r -> r.group(1))
                            .forEach(refs::add);
                }
        );

        log.info("已引用图片数量: {}", refs.size());
        // 引用图片为空时不操作，避免删错
        if (refs.isEmpty()) {
            return;
        }

        try (var stream = Files.list(Paths.get(appConfig.getFileDir(), "image/problem/"))) {
            for (var path : (Iterable<Path>) stream.filter(Files::isRegularFile)::iterator) {
                // 删除未引用的图片
                if (!refs.contains(path.getFileName().toString())) {
                    Files.deleteIfExists(path);
                    log.info("删除: {}", path);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

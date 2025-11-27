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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.Arrays;
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
        final var buf = new byte[32 * 1024];
        final var charBuf = CharBuffer.allocate(buf.length);
        final var decoder = StandardCharsets.UTF_8.newDecoder();
        final Set<String> refs = ConcurrentHashMap.newKeySet();

        jdbc.query((con) -> {
                    var sql = "select description from problem";
                    var ps = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                    ps.setFetchSize(200);
                    return ps;
                }, rs -> {
                    while (rs.next()) {
                        try (var in = rs.getBinaryStream("description")) {
                            var len = readLength(in, buf);
                            charBuf.clear();
                            decoder.decode(ByteBuffer.wrap(buf, 0, len), charBuf, true);
                            charBuf.flip();
                            IMG_PATTERN.matcher(charBuf)
                                    .results()
                                    .map(r -> r.group(1))
                                    .forEach(refs::add);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    }
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

    private int readLength(InputStream in, byte[] buf) throws IOException {
        int pos = 0;
        int r;

        while ((r = in.read(buf, pos, buf.length - pos)) != -1) {
            pos += r;
            if (pos == buf.length) {
                buf = Arrays.copyOf(buf, buf.length * 2);
            }
        }

        return pos;
    }
}

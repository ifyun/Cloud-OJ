package group._204.oj.fileserver.util;

import group._204.oj.fileserver.model.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FileUtil {
    public static void writeFile(MultipartFile src, File dst) throws IOException {
        if (!dst.exists() && !dst.mkdirs()) {
            throw new IOException(String.format("无法创建目录, path: %s.", dst.getAbsolutePath()));
        }

        src.transferTo(dst);
        log.info("文件已保存, path={}.", dst.getAbsolutePath());
    }

    public static boolean delFile(String filePath) {
        var file = new File(filePath);
        return !file.exists() || file.delete();
    }

    public static List<FileInfo> getFilesInfo(String path) {
        try (var files = Files.walk(Paths.get(path), Integer.MAX_VALUE)) {
            return files
                    .filter(p -> p.toFile().isFile())
                    .map(p -> {
                        var relativePath = p.toString()
                                .replaceAll("\\\\", "/")
                                .replace(path, "");
                        return new FileInfo(relativePath, p.toFile().lastModified());
                    }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ArrayList<>(0);
        }
    }
}

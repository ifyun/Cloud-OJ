package group._204.oj.fileserver.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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
        File file = new File(filePath);
        return !file.exists() || file.delete();
    }
}

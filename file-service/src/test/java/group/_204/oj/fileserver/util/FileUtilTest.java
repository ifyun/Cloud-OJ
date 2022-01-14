package group._204.oj.fileserver.util;

import group._204.oj.fileserver.model.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class FileUtilTest {

    @Value("${project.file-dir}")
    private String fileDir;

    @Test
    void getFilesInfo() {
        List<FileInfo> list = FileUtil.getFilesInfo(fileDir + "test_data");
        for (FileInfo i : list) {
            log.info(i.toString());
        }
    }
}
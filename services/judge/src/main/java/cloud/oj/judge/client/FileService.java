package cloud.oj.judge.client;

import cloud.oj.judge.entity.FileInfo;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "storage", path = "test_data")
public interface FileService {

    @GetMapping("info")
    List<FileInfo> dataList();

    @GetMapping("download{path}")
    Response downloadFile(@PathVariable String path);
}

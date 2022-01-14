package group._204.oj.judge.client;

import feign.Response;
import group._204.oj.judge.model.FileInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "file-service", path = "test_data")
public interface FileService {

    @GetMapping("info")
    List<FileInfo> dataList();

    @GetMapping("download{path}")
    Response downloadFile(@PathVariable String path);
}

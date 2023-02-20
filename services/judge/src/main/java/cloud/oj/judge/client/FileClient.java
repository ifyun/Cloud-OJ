package cloud.oj.judge.client;

import cloud.oj.judge.entity.FileInfo;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileClient {
    Flux<DataBuffer> getAllFileInfo();

    Mono<FileInfo> getFileInfo(String file);

    Flux<DataBuffer> downloadFile(String path);
}

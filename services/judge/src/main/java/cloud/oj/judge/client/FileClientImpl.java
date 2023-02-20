package cloud.oj.judge.client;

import cloud.oj.judge.entity.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@ConditionalOnProperty(prefix = "app", name = "file-sync")
public class FileClientImpl implements FileClient {
    private static final String BASE_URL = "lb://storage";

    private final WebClient webClient;

    @Autowired
    public FileClientImpl(WebClient.Builder builder) {
        var factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        webClient = builder.uriBuilderFactory(factory).baseUrl(BASE_URL).build();
    }

    @Override
    public Flux<DataBuffer> getAllFileInfo() {
        return webClient.get()
                .uri("/test_data/file_info_list")
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToFlux(DataBuffer.class);
    }

    @Override
    public Mono<FileInfo> getFileInfo(String file) {
        return webClient.get()
                .uri("/test_data/file_info?file=" + file)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FileInfo.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)));
    }

    @Override
    public Flux<DataBuffer> downloadFile(String path) {
        return webClient.get()
                .uri("/test_data/download" + path)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(3)));
    }
}

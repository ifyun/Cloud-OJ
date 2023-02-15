package cloud.oj.fileservice.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.File;

@Slf4j
@Configuration
public class FileServerConfig extends WebMvcConfigurationSupport {

    @Value(("${app.file-dir}"))
    private String fileDir;

    @PostConstruct
    private void init() {
        if (!fileDir.endsWith("/")) {
            fileDir += '/';
        }

        var dir = new File(fileDir);

        if (dir.exists()) {
            return;
        }

        if (dir.mkdirs()) {
            log.info("Create {} successful", fileDir);
        } else {
            log.error("Failed to create {}", fileDir);
        }
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/test_data/download/**")
                .addResourceLocations("file:" + fileDir + "test_data/");
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:" + fileDir + "image/");
        super.addResourceHandlers(registry);
    }
}

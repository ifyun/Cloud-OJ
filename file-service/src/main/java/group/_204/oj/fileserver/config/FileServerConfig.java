package group._204.oj.fileserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.PostConstruct;

@Configuration
public class FileServerConfig extends WebMvcConfigurationSupport {

    @Value(("${project.file-dir}"))
    private String fileDir;

    @PostConstruct
    private void init() {
        if (!fileDir.endsWith("/")) {
            fileDir += '/';
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

package group._204.oj.fileserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class FileServerConfig extends WebMvcConfigurationSupport {

    @Value(("${project.file-dir}"))
    private String fileDir;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/test_data/file/**")
                .addResourceLocations("file:" + fileDir + "test_data/");
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:" + fileDir + "image/");
        super.addResourceHandlers(registry);
    }
}

package top.cloudli.fileserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class FileServerConfig extends WebMvcConfigurationSupport {

    @Value("${project.test-data-dir}")
    private String testDataDir;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/test_data/file/**")
                .addResourceLocations("file:" + testDataDir);
        super.addResourceHandlers(registry);
    }
}

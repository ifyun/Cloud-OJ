package cloud.oj.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * 静态文件服务配置
 */
@Configuration
public class StaticResourceConfig implements WebFluxConfigurer {

    private final String fileDir;

    public StaticResourceConfig(AppConfig appConfig) {
        this.fileDir = appConfig.getFileDir();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file/data/download/**")
                .addResourceLocations("file:" + fileDir + "data/");
        registry.addResourceHandler("/file/img/**")
                .addResourceLocations("file:" + fileDir + "image/");
    }
}

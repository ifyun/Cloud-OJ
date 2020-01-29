package top.cloudli.cloudojweb.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.Resource;

@Slf4j
@Configuration
public class ThymeleafConfig {

    @Resource
    private Environment env;

    @Resource
    private void configThymeleafViewResolver(ThymeleafViewResolver thymeleafViewResolver) {
        if (thymeleafViewResolver != null) {
            String gatewayHost = env.getProperty("project.gateway-host");
            log.info("设置 Thymeleaf 全局变量：{}={}", "gateway_host", gatewayHost);
            thymeleafViewResolver.addStaticVariable("gateway_host", gatewayHost);
        }
    }
}

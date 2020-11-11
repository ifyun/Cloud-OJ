package group._204.oj.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Primary
@Configuration
@EnableSwagger2
public class ApiDocConfig implements SwaggerResourcesProvider {

    private static final String[][] SERVICES = {
            {"manager-service", "/api/manager"},
            {"judge-service", "/api/judgement"},
            {"file-server", "/api/file"}
    };

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();

        for (String[] service : SERVICES) {
            resources.add(swaggerResource(service[0], service[1] + "/v2/api-docs")
            );
        }

        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        return swaggerResource;
    }
}

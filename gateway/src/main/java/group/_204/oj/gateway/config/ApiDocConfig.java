package group._204.oj.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Primary
@Configuration
public class ApiDocConfig implements SwaggerResourcesProvider {

    private static final String[][] SERVICES = {
            {"GATEWAY", "/api/auth"},
            {"MANAGER-SERVICE", "/api/manager"},
            {"FILE-SERVICE", "/api/file"},
            {"JUDGE-SERVICE", "/api/judgement"}
    };

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();

        for (String[] service : SERVICES) {
            resources.add(swaggerResource(service[0], service[1] + "/v2/api-docs"));
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

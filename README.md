## Cloud Online Judge

基于 Spring Cloud 微服务的 Online Judge。

### 用到的组件

- 服务注册与发现：Eureka；
- 负载均衡：Zuul with Ribbon；
- 路由网关：Zuul；
- ORM：MyBatis；
- REST API：Spring MVC；
- 用户验证：Spring Security with JWT;
- Web UI：Thymeleaf。

### 各个服务的端口（DEV）

Service         | Port
----------------|------
register-center | 8761
file-server     | 8000
manager-service | 8001
judge-service   | 8002
api-gateway     | 5000
cloud-oj-web    | 8080

生产环境的服务统一使用 `8000` 端口，Docker 部署。
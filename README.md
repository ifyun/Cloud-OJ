## Cloud Online Judge

基于 Spring Cloud 的微服务 Online Judge。

### 用到的组件

- 服务注册于发现：Eureka；
- 负载均衡：Feign；
- 路由网关：Zuul；
- ORM：MyBatis；
- REST API：Spring MVC；
- 权限控制：Spring Security with JWT。

### 各个服务的端口（DEV）

Service         | Port
----------------|------
register-center | 8761
file-server     | 8000
manager-service | 8001

生产环境的服务统一使用 `8000` 端口，Docker 部署。
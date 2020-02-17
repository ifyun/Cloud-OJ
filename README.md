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

### 各个服务的端口

Service         | Port(Dev) | Port(Prod)
----------------|-----------|-------------
register-center | 8761      | 8761:8761
monitor-service | 5000      | 5000:5000
manager-service | 8001      | none:8000
file-server     | 8000      | none:8000
judge-service   | 8002      | none:8000
api-gateway     | 80        | 80:80
cloud-oj-web    | 8080      | none:8000

使用 Docker 部署时，`file-server` 和 `judge-service` 要在同一个主机上，且测试数据目录必须是同一个。
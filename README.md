# Cloud-OJ Source Code

Cloud-OJ 项目源码。

自动部署脚本传送门：[Cloud-OJ Deploy Script](https://github.com/imcloudfloating/Cloud-OJ-Docker)。

## 使用的组件

- 服务注册与发现：Eureka
- 负载均衡：Zuul with Ribbon
- 路由网关：Zuul
- 权限验证：Spring Security with JWT
- 服务监控：Spring Boot Admin
- ORM：MyBatis
- 数据库：MySQL
- 前端：Layui、Thymeleaf

Framework          | Version
-------------------|-----------------
Spring Boot        | 2.2.2.RELEASE
Spring Cloud       | Hoxton.RELEASE
Spring Boot Admin  | 2.2.1

## 服务的端口

Service         | Port
----------------|-----------
register-center | 8761
monitor-service | 5000
file-server     | 8000
manager-service | 8001
judge-service   | 8002
cloud-oj-web    | 8080
api-gateway     | 80

> 如果你改动了端口，同时修改 api-gateway 的配置文件。

## 食用方法

### Step 1

首先将根目录 `pom.xml` 中的以下内容注释掉：

```xml
<modules>
    <module>...</module>
    ...
</modules>
```

- 在 `Maven` 窗口中展开 `Cloud OJ (root)` 中的 `Lifecycle`，执行 `install`；
- 取消上一步注释的内容。

> 不取消注释也没事，只是无法自动执行子模块的生命周期。

### Step 2

搭建数据库和消息队列，两种方式：

1. 安装 Docker，直接运行 `database` 目录下的 `start-mysql&rabbitmq.cmd`；
2. 手动安装 RabbitMQ 和 MySQL，使用 `oj-database.sql` 建库建表。

### Step 3

运行服务。
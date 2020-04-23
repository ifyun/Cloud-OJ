# Cloud-OJ Project

Cloud-OJ 项目源码。

自动部署脚本传送门：[Cloud-OJ Deploy Script](https://github.com/imcloudfloating/Cloud-OJ-Docker)。

## 使用的组件

- 服务注册与发现：Eureka；
- 负载均衡：Zuul with Ribbon；
- 路由网关：Zuul；
- 权限验证：Spring Security with JWT;
- Web UI：Layui、Thymeleaf；
- 服务监控：Spring Boot Admin
- ORM：MyBatis（其实很想用 JPA）；
- 数据库：MySQL。

框架/组件名称        | 版本
-------------------|-----------------
Spring Boot        | 2.2.2.RELEASE
Spring Cloud       | Hoxton.RELEASE
Spring Boot Admin  | 2.2.1

## 各个服务的端口

Service         | Port
----------------|-----------
register-center | 8761      
monitor-service | 5000   
file-server     | 8000         
manager-service | 8001      
judge-service   | 8002     
cloud-oj-web    | 8080     
api-gateway     | 80          

> 如果你改动了端口，请同时修改 api-gateway 的配置文件。

## 食用方法

### Step 1

首先将根目录 `pom.xml` 中的以下内容注释掉：

```xml
<modules>
    <module>...</module>
    ...
</modules>
```

### Step 2

在 `Maven` 窗口中展开 `Cloud OJ` 模块的 `Lifecycle`，执行 `install`，然后取消上一步中注释的内容。

> 不取消注释也没啥事，只是无法自动执行子模块的生命周期）。

### Step 3

创建数据库（建议使用 MySQL 8.0），执行 `OJ-Database.sql` 脚本。

### Step 4

Build and run.
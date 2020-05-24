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
- 前端：Layui

Framework          | Version
-------------------|-----------------
Spring Boot        | 2.2.2.RELEASE
Spring Cloud       | Hoxton.RELEASE
Spring Boot Admin  | 2.2.1

## 运行此项目

### Step 1

首先将根目录 `pom.xml` 中的以下内容注释掉：

```xml
<modules>
    <module>...</module>
    ...
</modules>
```

1. 在 `Maven` 窗口中展开 `Cloud OJ` 中的 `Lifecycle`，执行 `install`；
2. 取消上一步注释的内容。

> 不取消注释也没事，只是无法自动执行子模块的生命周期。

### Step 2

搭建数据库和消息队列，两种方式：

1. 安装 Docker，直接运行 `mysql` 目录下的 `start.cmd`；
2. 手动安装 RabbitMQ 和 MySQL，使用 `oj-database.sql` 建库建表。

设置测试数据目录临时目录：

修改 `file-server` 和 `judge-service` 的配置文件：

```yaml
project:
  test-data-dir: <测试数据存放目录>
  target-dir: <临时存放代码的目录>   # 这一项在 judge-service 中
```

### Step 3

Build & Run.

> 若需要正常运行判题，还需要安装各个语言的环境：
> - C/C++：gcc、g++，Windows 可以使用 [MinGW](http://www.mingw.org/)
> - Java：能跑此项目，肯定就有 JDK 了
> - Python：[任意版本](https://www.python.org/)
> - Bash：Windows 安装 [Git](https://git-scm.com/) 就有了
> - C#：[Mono](https://www.mono-project.com/)
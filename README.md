# Cloud-OJ Source Code

![Last commit](https://img.shields.io/github/last-commit/imcloudfloating/Cloud-OJ) ![Stars](https://img.shields.io/github/stars/imcloudfloating/Cloud-OJ) ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/imcloudfloating/Cloud-OJ/Java%20CI%20with%20Maven) ![License](https://img.shields.io/github/license/imcloudfloating/Cloud-OJ)

这是一个微服务架构的 Online Judge，基于 Spring Cloud。

自动部署脚本：[Deploy Script](https://github.com/imcloudfloating/Cloud-OJ-Docker)。

> 本系统参考了 [HUSTOJ](https://github.com/zhblue/hustoj)

## 支持语言

- C
- C++
- Java
- Python
- Bash Shell
- C#

## 使用的组件

- 服务注册与发现：Eureka
- 负载均衡：Zuul with Ribbon
- 路由网关：Zuul
- 权限验证：Spring Security with JWT
- 服务监控：Spring Boot Admin
- ORM：MyBatis
- 数据库：MySQL
- 前端：Layui

Framework/Components    | Version
------------------------|-----------------
Spring Boot             | 2.2.2.RELEASE
Spring Cloud            | Hoxton.RELEASE
Spring Boot Admin       | 2.2.1

## 使用指南

### Step 1

搭建数据库和消息队列，两种方式：

1. 安装 Docker，直接运行 `mysql` 目录下的 `start.cmd`；
2. 手动安装 RabbitMQ 和 MySQL，使用 `oj-database.sql` 建库建表。

设置测试数据目录临时目录：

修改 `file-server` 和 `judge-service` 的配置文件：

```yaml
project:
  test-data-dir: <测试数据存放目录>
  target-dir: <临时存放代码和可执行文件的目录>   # 这一项在 judge-service
```

### Step 2

安装各个语言的编译和运行环境：

- C/C++：gcc、g++，Windows 可以使用 [MinGW](http://www.mingw.org/)；
- Java：能跑此项目，肯定就有 JDK（设置好环境变量）；
- Python：网页中标的是3.5，可以使用 [任意版本](https://www.python.org/)；
- Bash：Windows 可以安装 [Git](https://git-scm.com/)；
- C#：[Mono](https://www.mono-project.com/)。

> 以上环境不安装也可以运行，只是影响判题的执行。

### Step 3

Build & Debug.

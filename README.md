# Cloud-OJ

![Last commit](https://img.shields.io/github/last-commit/imcloudfloating/Cloud-OJ?style=flat-square)
![Stars](https://img.shields.io/github/stars/imcloudfloating/Cloud-OJ?style=flat-square)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/imcloudfloating/Cloud-OJ/Java%20CI%20with%20Maven?style=flat-square)
![License](https://img.shields.io/github/license/imcloudfloating/Cloud-OJ?style=flat-square)

这是一个微服务架构的 Online Judge，基于 Spring Cloud 和 Vue.js。

> 本项目参考了 [HUSTOJ](https://github.com/zhblue/hustoj)。

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
- 路由网关：Zuul & Nginx
- 权限验证：Spring Security with JWT
- 服务监控：Spring Boot Admin
- ORM：MyBatis
- 数据库：MySQL
- 前端：Vue.js, Element UI

## 使用指南

### 自动部署

使用部署脚本(仅支持 Docker)：[Deploy Script](https://github.com/imcloudfloating/Cloud-OJ-Docker)

### 手动构建

#### Step 1

搭建数据库和消息队列，两种方式：

1. 安装 Docker，直接运行 `mysql` 目录下的 `start.cmd`（For Debug）
2. 手动安装 RabbitMQ 和 MySQL，使用 `oj-database.sql` 建库建表（For Deploy）

设置测试数据目录临时目录：

修改 `file-server`、`manager-service`、 `judge-service` 的配置文件：

```yaml
project:
  file-dir: <测试数据的存放目录>
  target-dir: <临时存放代码和可执行文件的目录>   # 此项在 judge-service
```

#### Step 2

安装各个语言的编译和运行环境：

语言         | 环境(编译器、SDK)   
------------|-----------------------------------------------------------
C/C++       | gcc、g++，Windows 可以使用[MinGW](http://www.mingw.org/)
Java        | 网页中标的是 1.8，可以使用任意版本
Python      | 网页中标的是 3.5，可以使用任意版本
Bash Shell  | Windows 环境可以使用 [Git Bash](https://git-scm.com/) 来调试
C#          | [Mono](https://www.mono-project.com/)，支持 Linux 和 Windows

> JDK 和 Mono 请务必设置好环境变量（以上环境不安装也可以运行，只是影响判题的执行）。

#### Step 3

Build Services：

```bash
mvn -B package '-Dmaven.test.skip=true' --file pom.xml
```

Build Web：

```bash
cd web && npm install && npm run build
```

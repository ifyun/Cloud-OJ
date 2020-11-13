# Cloud OJ

![Stars](https://img.shields.io/github/stars/imcloudfloating/Cloud-OJ?style=flat-square&logo=github)
![Top Languages](https://img.shields.io/github/languages/top/imcloudfloating/Cloud-OJ?style=flat-squre&logo=github)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/imcloudfloating/Cloud-OJ/Java%20CI%20with%20Maven?style=flat-square&logo=github)
![License](https://img.shields.io/github/license/imcloudfloating/Cloud-OJ?style=flat-square)
![Last Commit](https://img.shields.io/github/last-commit/imcloudfloating/Cloud-OJ?style=flat-square)

微服务架构的 Online Judge，基于 Spring Cloud、Vue.js、Docker。

> 本项目在功能和数据库结构上参考了 [HUSTOJ](https://github.com/zhblue/hustoj)

## 支持语言

Language        | Compiler/Interpreter/Version
----------------|-------------------------------
C               | gcc
C++             | g++ std=14
Java            | OpenJDK 1.8
Python          | 3.5
Bash            |
C#              | Mono
JavaScript      | Node v14
Kotlin          | 1.4.10

## 判题方式

本项目没有直接使用 `ptrace` 和 `seccomp` 来限制系统调用，而是构建一个包含各种语言运行环境的 Docker 镜像，
将测试数据和用户的代码挂载到容器进行编译和判题，以此达到沙盒的效果。

## 使用指南

### 自动部署

若没有二次开发的需求，推荐使用部署脚本(仅支持 Docker、Docker Swarm)：[Cloud Deploy Script](https://github.com/imcloudfloating/Cloud-OJ-Docker)

### 手动构建

#### 1. 搭建数据库和消息队列

两种方式：

1. 安装 Docker，执行 `mysql` 目录下的 `start.sh`
2. 手动安装 RabbitMQ 和 MySQL，使用 `./mysql/init/init-database.sql` 建库建表

设置数据目录，修改 `file-server`、`manager-service`、 `judge-service` 的配置文件（application-prod.yml）：

```yaml
project:
  file-dir: <测试数据和其他文件的存放目录>
  code-dir: <临时存放代码和可执行文件的目录>   # in judge-service
```

#### 2. 构建用于判题的 Docker 镜像

安装 Docker，构建 `runner` 镜像：

```bash
docker build -t runner ./docker/runner
```

`judge-service` 的配置文件中可以指定镜像：

```yaml
project:
  runner-image: ${RUNNER_IMAGE:runner}
```

> 如果不想构建镜像，可以直接使用 `registry.cn-hangzhou.aliyuncs.com/cloud_oj/runner`

#### 3. 构建、打包

构建服务（需要 Maven）：

```bash
mvn -B package '-Dmaven.test.skip=true' --file pom.xml
```

构建 Web（需要 Node.js）：

```bash
cd web && npm install && npm run build
```

### 4. 部署

- 运行服务，指定 `prod` 配置文件：`java -jar -Dspring.profiles.active=prod xxx.jar`
- Web 可使用 Nginx 部署

若需要扩展服务，请改用 NFS 存储 OJ 文件并在节点挂载(默认目录是：`/var/lib/cloud_oj`)

> **提示：judge-service 仅支持在 Linux 下运行**。

## Acknowledgement

[![JetBrains](./.assets/jetbrains.svg)](https://www.jetbrains.com/?from=CloudOJ)

Thanks to JetBrains' Open Source License support.

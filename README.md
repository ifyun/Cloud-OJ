# Cloud OJ

![Stars](https://img.shields.io/github/stars/imcloudfloating/Cloud-OJ?logo=github)
![Top Languages](https://img.shields.io/github/languages/top/imcloudfloating/Cloud-OJ?logo=github)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/imcloudfloating/Cloud-OJ/Java%20CI%20with%20Maven?logo=github)
![Codacy Badge](https://api.codacy.com/project/badge/Grade/3fb7e4c059c5431799b8863218750095)
![License](https://img.shields.io/github/license/imcloudfloating/Cloud-OJ)
![Last Commit](https://img.shields.io/github/last-commit/imcloudfloating/Cloud-OJ)

微服务架构的 Online Judge，基于 Spring Cloud、Vue.js、Docker。

> 本项目在功能和数据库结构上参考了[HUSTOJ](https://github.com/zhblue/hustoj)。

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

## 使用

### 自动部署

推荐使用 Docker 一键部署，可参考[部署文档](https://github.com/imcloudfloating/Cloud-OJ/wiki/部署)。

### 手动构建

#### 1. 搭建数据库和消息队列

安装 RabbitMQ 和 MySQL，使用 `./db/init/init-database.sql` 文件建库建表。

设置数据目录，修改 `file-service`、`manager-service`、 `judge-service` 的配置文件（application-prod.yml）：

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

#### 4. 部署

- 运行服务，指定 `prod` 配置文件：`java -jar -Dspring.profiles.active=prod xxx.jar`
- Web 可使用 Nginx 部署

若需要扩展服务，请改用 NFS 存储 OJ 文件并在节点挂载(默认目录是：`/var/lib/cloud_oj`)。

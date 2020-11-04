# Cloud OJ

![Stars](https://img.shields.io/github/stars/imcloudfloating/Cloud-OJ?style=flat-square&logo=github)
![Top Languages](https://img.shields.io/github/languages/top/imcloudfloating/Cloud-OJ?style=flat-squre&logo=github)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/imcloudfloating/Cloud-OJ/Java%20CI%20with%20Maven?style=flat-square&logo=github)
![License](https://img.shields.io/github/license/imcloudfloating/Cloud-OJ?style=flat-square)
![Last Commit](https://img.shields.io/github/last-commit/imcloudfloating/Cloud-OJ?style=flat-square)

微服务架构的 Online Judge，基于 Spring Cloud、Vue.js、Docker。

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

#### 1. 搭建数据库和消息队列

两种方式：

1. 安装 Docker，执行 `mysql` 目录下的 `start.sh`
2. 手动安装 RabbitMQ 和 MySQL，使用 `./mysql/init/init-database.sql` 建库建表

设置数据目录，修改 `file-server`、`manager-service`、 `judge-service` 的配置文件（application-prod.yml）：

```yaml
project:
  file-dir: <测试数据和其他文件的存放目录>
  code-dir: <临时存放代码和可执行文件的目录>   # 此项在 judge-service
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

**提示：judge-service 仅支持在 Linux 下运行**。

#### 3. 构建、打包

构建服务（需要 Maven）：

```bash
mvn -B package '-Dmaven.test.skip=true' --file pom.xml
```

> 启动服务时，指定 `prod` 配置文件：`java -jar -Dspring.profiles.active=prod xxx.jar`

构建 Web（需要 Node.js）：

```bash
cd web && npm install && npm run build
```

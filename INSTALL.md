# 构建 & 安装

需要以下环境:

- Linux
- CMake 3.16+
- Maven 3.8+
- OpenJDK 17
- Node.js v18
- Boost

## 直接安装

建议使用 Docker 运行，无论是单机运行或是扩展服务都比较方便。

### 安装构建环境

Debian 系列：

```bash
curl -sL https://deb.nodesource.com/setup_lts.x | sudo bash -
sudo apt-get update
sudo apt-get install -y \
  build-essential \
  cmake \
  libboost-iostreams-dev \
  libboost-program-options-dev \
  maven \
  openjdk-17-jdk-headless \
  nodejs
```

Fedora 系列：

```bash
sudo dnf install -y \
  gcc \
  g++ \
  cmake \
  make \
  boost-devel \
  maven \
  java-17-openjdk-devel \
  nodejs
```

### Build & Install

下载源码，运行：

```bash
./build install
```

以上命令将项目安装到 `/usr/local/cloud-oj`，同时安装 nginx、 supervisor、rabbitmq-server、mariadb-server。

- 测试数据位置：`/var/lib/cloud-oj`
- 配置文件位置：`/etc/cloud-oj`

> 若需要修改 MariaDB、RabbitMQ 密码，修改后请在 `/etc/cloud-oj/supervisord.conf` 文件中设置。

运行：

```bash
sudo supervidsord -c /etc/cloud-oj/supervisord.conf
```

完整的语言支持还需要安装以下软件包：

- [kotlin-compiler](https://github.com/JetBrains/kotlin/releases)
- [mono-devel](https://www.mono-project.com/)
- [go](https://go.dev/)

> 系统默认管理员账号和密码均为 `admin`。

## Docker 运行

构建过程在容器中运行，你只需要安装好 Docker 即可。

> 宿主机的 `~/.m2` 目录会映射到构建环境的容器中，你可以创建 `~/.m2/settings.xml` 来设置 Maven 中央仓库的镜像。

```bash
./build docker
```

构建完成后，编排文件将复制到 `/usr/local/cloud-oj` 目录。

运行：

```bash
cd /usr/local/cloud-oj
docker-compose up -d
```

> 首次运行时，你可以在 `.env` 文件中修改 MariaDB、RabbitMQ 的用户名和密码。

部分端口未映射到宿主机，如有必要可参考下表：

| Service  | Port(s)     |
|----------|-------------|
| gateway  | 8080        |
| core     | 8180        |
| judge    | 8280        |
| mariadb  | 3306        |
| rabbitmq | 5672, 15672 |

## 开启 HTTPS

若是 Docker 安装，建立一个目录存放你的证书和私钥(`cert.pem`, `private.key`)

修改编排文件的 `web` 部分：

```yaml
ports:
  - "80:80"
  - "443:443"
volumes:
  - "宿主机证书目录:/ssl"
environment:
  API_HOST: "gateway:8080"
  ENABLE_HTTPS: "true"
  EXTERNAL_URL: "你的域名/外部IP"
```

开启 HTTPS 后，80 端口会重定向到 443。

若是直接安装，在 `/etc/cloud-oj/nginx.conf` 中配置。

## 环境变量说明

### gateway

`TOKEN_VALID_TIME`

JWT 有效时间，默认值为 `4`，单位：小时

`JUDGE_CPUS`

判题线程使用的 CPU，用数组表示：

```
JUDGE_CPUS=[0]          # 使用所有 CPU
JUDGE_CPUS=[n]          # 使用 n 个 CPU，从 CPU-0 开始
JUDGE_CPUS=[0,1,2]      # 使用 CPU-0, CPU-1, CPU-2
```

`API_HOST`

gateway 服务的地址 + 端口（仅 web 容器使用）

`JVM_OPTS`

Java 虚拟机参数，eg: -Xmx500m

`CONSUL_HOST`

Consul 注册中心的地址，默认为 `localhost`

`USE_IP`

使用 IP 地址注册服务，默认值为 `false`

`SERVICE_IP`

`USE_IP` 为 `true` 时生效，可以指定该服务的 IP 地址，默认值为 `spring.cloud.client.ip-address`
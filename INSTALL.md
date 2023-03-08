# 构建 & 安装

需要以下环境:

- Linux
- CMake 3.16+
- Maven 3.8+
- OpenJDK 17
- Node.js v18
- Boost

> 推荐使用 Debian(Ubuntu)、Fedora 发行版。

可使用以下脚本安装环境：

```bash
./dev/install-env
```

## 直接安装

下载源码，在项目根目录运行：

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

## Docker 运行

安装 Docker 并构建镜像：

```bash
./build docker
```

构建完成后，编排文件将复制到 `/usr/local/cloud-oj/docker` 目录。

运行：

```bash
cd /usr/local/cloud-oj
docker-compose up -d
```

> 首次运行时，你可以在 `.env` 文件中修改 MariaDB、RabbitMQ 的用户名和密码。

部分端口未映射到宿主机，如有必要可参考下表：

| Service  | Port(s)     |
|----------|-------------|
| registry | 8761        |
| gateway  | 8080        |
| core     | 8180        |
| storage  | 8280        |
| judge    | 8380        |
| mysql    | 3306        |
| rabbitmq | 5672, 15672 |

## 开启 HTTPS

若是 Docker 安装，建立一个目录存放你的证书，在 `docker-compose.yml` 文件的 `web` 中加入：

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
  SSL_CERT: "ssl_cert.pem"
  SSL_KEY: "ssl_key.key"
```

开启 HTTPS 后，80 端口会重定向到 443。

若是直接安装，在 `/etc/cloud-oj/nginx.conf` 中配置。
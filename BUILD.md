# 构建 & 运行

构建过程在容器中进行，你只需要安装好 Docker 即可

## 构建 Docker 镜像

```bash
./build docker
```

构建完成后，编排文件将复制到 `~/cloud-oj` 目录

运行：

```bash
cd ~/cloud-oj
docker compose up -d
```

首次运行时，你可以在 `.env` 文件中修改 MariaDB、RabbitMQ 的用户名和密码

**系统默认管理员账号和密码均为 `admin`**

部分端口未映射到宿主机，如有必要可参考下表：

| Service  | Port(s)     |
|----------|-------------|
| Consul   | 8500        |
| gateway  | 8080        |
| core     | 8180        |
| judge    | 8280        |
| mariadb  | 3306        |
| rabbitmq | 5672, 15672 |

### 仅构建指定的镜像

加入第二个参数：

```bash
./build docker web
```

以上命令仅构建 `cloudoj-web` 镜像

可选参数：

- web
- core
- gateway
- judge
- mariadb

## 开启 HTTPS

建立一个目录存放你的证书和私钥(命名为 `cert.pem`, `private.key`)

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
  EXTERNAL_URL: "你的域名"
```

开启 HTTPS 后，80 端口会重定向到 443

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

默认使用 4 个线程，请根据可用内存量来设置

`API_HOST`

gateway 服务的地址 + 端口（仅 web 容器使用）

`JVM_OPTS`

Java 虚拟机参数，eg: -Xmx500m

`CONSUL_HOST`

Consul 注册中心的地址

`USE_IP`

使用 IP 地址注册服务，默认值为 `false`

`SERVICE_IP`

`USE_IP` 为 `true` 时生效，指定当前服务的 IP 地址，默认值为 `spring.cloud.client.ip-address`

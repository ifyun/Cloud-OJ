# Cloud OJ

![Codacy Badge](https://img.shields.io/codacy/grade/3fb7e4c059c5431799b8863218750095?logo=codacy)
![CMake Build](https://img.shields.io/github/actions/workflow/status/imcloudfloating/Cloud-OJ/cmake.yml?label=cmake%20build&logo=cmake&logoColor=blue)
![Maven Build](https://img.shields.io/github/actions/workflow/status/imcloudfloating/Cloud-OJ/maven.yml?label=maven%20build&logo=apache-maven&logoColor=red)
![Vite Build](https://img.shields.io/github/actions/workflow/status/imcloudfloating/Cloud-OJ/node.js.yml?label=vite%20build&logo=vite)
![Platform](https://img.shields.io/badge/platform-linux--64-blueviolet?logo=linux&logoColor=white)
![Stars](https://img.shields.io/github/stars/imcloudfloating/Cloud-OJ?logo=github)
![Top Languages](https://img.shields.io/github/languages/top/imcloudfloating/Cloud-OJ?logo=github)
![Last Commit](https://img.shields.io/github/last-commit/imcloudfloating/Cloud-OJ?logo=github)

Cloud OJ 是一个微服务架构的 Online Judge 系统，基于 Spring Cloud、Vue.js、UNIX API。

- 可直接安装或 Docker 运行
- 代码高亮
- 亮色/暗色主题
- 可扩展的判题节点

<table>
<tr>
  <td><img src="./.assets/light.png" alt="light"></td>
  <td><img src="./.assets/dark.png" alt="dark"></td>
</tr>
</table>

## 语言支持

- C
- C++
- Java
- Python
- Bash Shell
- C#
- JavaScript
- Kotlin
- Go

## 构建 & 安装

需要以下环境:

- Ubuntu 22.04+
- CMake 3.16+
- Maven 3.8+
- OpenJDK 17+
- Node.js v16+

执行以下脚本可以安装环境：

```bash
./dev/install-env
```

### 直接安装

下载源码，在项目根目录运行：

```bash
./build install
```

以上命令将项目安装到 `/usr/local/cloud-oj`，同时安装 nginx、 supervisor、rabbitmq-server、mysql-server。

- 测试数据位置：`/var/lib/cloud-oj`
- 配置文件位置：`/etc/cloud-oj`

你需要手动执行以下操作：

1. 设置 MySQL 用户和密码，使用 `dev/sql` 目录中的脚本初始化数据库
2. 创建 RabbitMQ 用户，并赋予 `administrator` 权限
3. 在 `/etc/cloud-oj/supervisord.conf` 文件中设置 MySQL、RabbitMQ 用户和密码

运行：

```bash
sudo supervidsord -c /etc/cloud-oj/supervisord.conf
```

### Docker 运行

安装 Docker 并构建镜像：

```bash
./build docker
```

构建完成后，在 `/usr/local/cloud-oj/docker` 目录中将有以下文件：

```text
.
├── docker-compose.yml
├── .env
└── sql
```

运行：

```bash
cd /usr/local/cloud-oj
docker-compose up -d
```

> 首次运行时，你可以在 `.env` 文件中修改 MySQL 和 RabbitMQ 的用户名和密码。

部分端口未映射到宿主机，如有必要可参考下表：

| Service  | Port(s)     |
|----------|-------------|
| registry | 8761        |
| gateway  | 8080        |
| core     | 8081        |
| storage  | 8082        |
| judge    | 8083        |
| mysql    | 3306        |
| rabbitmq | 5672, 15672 |

### 开启 HTTPS

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

## Technologies

- [Spring](https://spring.io/)
- [RabbitMQ](https://www.rabbitmq.com/)
- [Boost](https://www.boost.org/)
- [Vue 3](https://vuejs.org/)
- [Naive UI](https://naiveui.com/)
- [CodeMirror 5](https://codemirror.net/5/)
- [KaTeX](https://katex.org/)
- [Apache Echarts](https://echarts.apache.org/)
- [highlight.js](https://highlightjs.org/)
- [markdown-it](https://github.com/markdown-it/)

---

Thanks JetBrains for
[CLion](https://www.jetbrains.com/clion/),
[IntelliJ IDEA](https://www.jetbrains.com/idea/),
[WebStorm](https://www.jetbrains.com/webstorm/)
licenses.

<a href="https://www.jetbrains.com/">
<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_square.svg" alt="JetBrains Logo.">
</a>

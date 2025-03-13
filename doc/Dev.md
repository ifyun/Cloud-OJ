# 设置开发环境

判题程序需要 Linux 环境，建议使用 Debian。

使用 fnm 安装 Node.js：

```bash
curl -o- https://fnm.vercel.app/install | bash
fnm install 22
```

使用包管理器安装 C / C++ / CMake / Java / Maven

```bash
sudo apt install cmake build-essential openjdk-17-jdk-headless maven 
```

创建必要的目录：

```bash
sudo mkdir /opt/.m2 /opt/cloud-oj
sudo chmod 777 /opt/.m2
sudo chmod 777 /opt/cloud-oj
ln -s /opt/.m2 /root/.m2
ln -s /opt/.m2 ~/.m2
ln -s /opt/cloud-oj /root/.local/cloud-oj
ln -s /opt/cloud-oj ~/.local/cloud-oj
```

## 设置 Judge 运行环境

C / C++ / Java / JS 环境已在前面的步骤中安装。

### Kotlin Native

```bash
curl -LJO https://github.com/JetBrains/kotlin/releases/download/v2.1.10/kotlin-native-prebuilt-linux-x86_64-2.1.10.tar.gz \
  && sudo tar -C /usr/local -xzf kotlin-native-prebuilt-linux-x86_64-2.1.10.tar.gz \
    --transform 's/kotlin-native-prebuilt-linux-x86_64-2.1.10/kotlin/' \
  && sudo ln -s /usr/local/kotlin/bin/kotlinc-native /usr/bin/kotlinc-native \
  && sudo ln -s /usr/local/kotlin/bin/run_konan /usr/bin/run_konan
```

编译一个不存在的文件触发依赖下载：

```bash
sudo kotlinc-native nothing.kt
```

> Kotlin Native 依赖会下载到 `~` 目录，而 Judge Service 在 `root` 权限下运行，因此需要以 `root` 运行以上命令。

### Golang

```bash
curl -LJO https://golang.google.cn/dl/go1.24.1.linux-amd64.tar.gz \
  && tar -C /usr/local -xzf go1.24.1.linux-amd64.tar.gz \
  && ln -s /usr/local/go/bin/go /usr/bin/go
```

### C#

C# 使用 `dotnet-sdk-8.0` 编译和运行。

1.配置软件源：

```bash
wget https://packages.microsoft.com/config/debian/12/packages-microsoft-prod.deb \
  -O packages-microsoft-prod.deb \
  && sudo dpkg -i packages-microsoft-prod.deb \
  && rm packages-microsoft-prod.deb
```

2.安装 SDK：

```bash
sudo apt-get update \
  && sudo apt-get install -y dotnet-sdk-8.0
```

3.创建单文件编译脚本：

```bash
sudo echo -n "dotnet /usr/share/dotnet/sdk/$(dotnet --version)/Roslyn/bincore/csc.dll " > /bin/csc \
  && echo -n "/r:/usr/share/dotnet/sdk/$(dotnet --version)/ref/netstandard.dll " >> /bin/csc \
  && echo '"$@"' >> /bin/csc
```

4.创建运行时配置文件：

将项目根目录中的 `dotnet.runtimeconfig.json` 复制到 `/etc` 目录，运行时配置文件将在判题时链接到工作目录。

# Judge Runner

OJ 判题程序，代码中未做调用限制，可与容器配合使用。

## 生成可执行文件

1. 准备 Linux 环境并安装 cmake、gcc、g++
2. 运行 `cmake-init.sh` 或者用 CLion 打开
3. 切换到 `cmake-build-release` 目录，执行 `cmake` 生成可执行文件或者通过 IDE 生成

## 使用

```bash
runner <cmd> <timeout> <memory> <max_memory> <output_size> <data_dir>
```

- `cmd`: 要执行的命令, 用 `@` 代替 <kbd>空格</kbd>
- `timeout`: 运行时间，单位：毫秒
- `memory`: 内存限制，此项用于判断是否超限，单位：MB
- `max_memory`: 此项为实际的内存限制，超出此限制程序会中断并返回非零值，单位：MB
- `output_size`: 输出限制，单位：KB
- `data_dir`: 测试数据目录，包含 `*.in`、 `*.out` 文件

### 示例

Python:

```bash
runner python@Solution.py 500 64 512 8192 /var/test_data
```

Java:

```bash
runner java@-Xms8m@-Xmx512m@Solution 1000 64 512 /var/test_data
```

输出和结果分别保存在工作目录的 `*.out` 和 `result.json` 文件，若返回值不为 0 说明出现了错误。

`result.json` 示例:

```json
[
  {
    "status": 0,
    "timeUsed": 17,
    "memUsed": 9344
  }
]
```

> `result.json` 是一个数组，长度和输入数据(`*.in`)的数量相同，若输入数据和输出数据数量不等，或者没有输出数据，程序会直接退出。

`memUsed` 单位为 KB，`status` 的取值如下:

- 0: 通过
- 1: 答案错误
- 2: 超时
- 3: 超内存
- 4: 运行错误

## 构建 Docker 镜像

先生成可执行文件，然后执行 `docker build -t judge-runner .`

### 使用容器判题

示例：

```bash
docker run --rm --network none \
-v /tmp/code:/code \
-v /var/test_data:/test_data:ro \
-w /code \
judge-runner \
runner python@Solution.py 500 64 1024 8192 /test_data
```

将代码和测试数据挂载到容器即可，测试数据以只读方式挂载。

### 使用容器编译代码

C++:

```bash
docker run --rm --network none \
-v /tmp/code:/code \
-w /code \
judge-runner g++ -std=c++17 -o Solution Solution.cpp
```

Java:

```bash
docker run --rm --network none \
-v /tmp/code:/code \
-w /code \
judge-runner javac Solution.java
```

C#:

```bash
docker run --rm --network none \
-v /tmp/code:/code \
-w /code \
judge-runner mcs Solution.cs
```
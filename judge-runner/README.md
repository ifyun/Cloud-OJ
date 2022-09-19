# Judge Runner

OJ 判题程序。

## build

1. 准备 Linux 环境并安装 cmake、gcc、g++，Ubuntu 可以使用以下命令

   ```bash
   sudo apt-get install cmake build-essential libboost-iostreams-dev libboost-program-options-dev
   ```

2. 执行 `cmake-build.sh` 生成可执行文件

生成的可执行文件有两个：

- judge： 判题程序
- judged：守护进程

## install

```bash
sudo ./cmake-build.sh install
```
- 可执行文件将被复制到 `/opt/cloud-oj/bin` 目录
- 同时会在 `/usr/bin` 目录创建符号链接

## judge

```bash
judge <options>
```

- `--cmd`: 要执行的命令, 用 `@` 代替 <kbd>空格</kbd>
- `--lang`: 语言，用于加载系统调用规则
- `--time`: 运行时间，单位：毫秒
- `--ram`: 内存限制，此项用于判断是否超限，单位：MB
- `--output`: 输出限制，单位：MB
- `--work-dir`: 工作目录，用户程序所在目录
- `--data`: 测试数据目录，包含 `*.in`、 `*.out` 文件
- `--cpu`: CPU 核心编号
- `--help`: 显示帮助

### 示例

长参数：

```bash
judge --cmd=python@Solution.py \
      --time=100 \
      --ram=16 \
      --output=1 \
      --work-dir=/tmp/solution \
      --data=/tmp/data
```

短参数：

```bash
judge -c java@-Xms16m@-Xmx64m@Solution \
      -t 200 \
      -m 64 \
      -o 8 \
      -w /tmp/solution \
      -d /tmp/data
```

### 判题结果

- 用户的输出保存在工作目录的 `*.out`
- 判题结果保存在工作目录的 `result.json` 并打印到标准输出流

若判题发生错误，错误信息会打印到标准错误流，若没有任何输出，可以通过 `echo $?` 查看返回值：

- 返回 1 表示用户程序出现了错误
- 返回 -1 表示判题程序出现了错误

`result.json` 示例:

```json
{
  "code": 0,
  "status": 0,
  "result": "AC",
  "total": 1,
  "passed": 1,
  "passRate": 1,
  "time": 1,
  "memory": 560
}
```

- `code`: 0 表示没有错误
- `time`: 运行时间，单位：毫秒
- `memory`: 内存占用，单位：KB
- `total`: 测试点数量
- `passed`: 通过测试点数量

> `time` 和 `memory` 为所有测试点中的最大值。

`status` 的取值如下:

- 0: 通过
- 1: 答案错误
- 2: 超时
- 3: 内存超限
- 4: 输出超限
- 5: 部分通过

如果发生错误，`result.json` 如下：

```json
{
  "code": 1,
  "error": "错误信息"
}
```

`code`:

- `1` 表示运行错误(用户程序运行出错)
- `2` 表示内部错误(判题程序自身出错)

## judged

`judged` 使用 UNIX Domain Socket 通信，接收参数调用 `judge` 判题并将结果写回客户端。

- `judged` 默认创建一个大小等于 CPU 核心数的线程池
- Socket 文件路径为 `/var/run/judge.sock`
- 客户端发送的数据为 `judge` 的选项参数(字节流)，用空格分隔

直接执行 `judged` 会在前台运行，日志输出到终端：

```text
2022-09-19 11:00:35  INFO [      main] judged start, listen /var/run/judge.sock.
2022-09-19 11:00:35  INFO [      main] create pool, size=16.
```

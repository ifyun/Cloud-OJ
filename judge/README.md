# Judge Runner

Online Judge 判题程序。

## Build

1. 准备 Linux 环境并安装 cmake、make、gcc、g++，Ubuntu 可以使用以下命令

   ```bash
   sudo apt-get install cmake build-essential
   ```

2. 运行 `./build` 生成可执行文件

## Install

```bash
sudo ./build install
```

- 可执行文件将被复制到 `/usr/local/bin` 目录

## judge 用法

```bash
judge <options>
```

- `--cmd`: 要执行的命令, 用 `@` 代替 <kbd>空格</kbd>
- `--lang`: 语言，用于加载系统调用规则
- `--time`: CPU 时间限制，单位：毫秒
- `--ram`: 内存限制，此项用于判断是否超限，单位：MB
- `--output`: 输出限制，单位：MB
- `--workdir`: 工作目录，用户程序所在目录
- `--data`: 测试数据目录，包含 `*.in`、 `*.out` 文件
- `--cpu`: 用哪个 CPU 核心

`--lang` 取值：

| 参数 | 语言         |
|----|------------|
| 0  | C          |
| 1  | C++        |
| 2  | Java       |
| 3  | Python     |
| 4  | Bash       |
| 5  | C#         |
| 6  | JavaScript |
| 7  | Kotlin     |
| 8  | Go         |

### 示例

长参数：

```bash
judge --cmd=python3@Solution.py \
      --time=100 \
      --ram=16 \
      --output=1 \
      --workdir=/tmp/solution \
      --data=/tmp/data
```

短参数：

```bash
judge -c java@-Xmx256m@Solution \
      -t 200 \
      -m 64 \
      -o 8 \
      -w /tmp/solution \
      -d /tmp/data
```

### 判题结果

- 用户的输出保存在工作目录的 `*.out`
- 判题结果保存在工作目录的 `result.json` 并打印到 `stdout`

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
- `time`: 运行时间，单位：微秒
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

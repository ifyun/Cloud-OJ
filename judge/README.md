# Judge Runner

Online Judge 判题程序。

## Build

1. 准备 Linux 环境并安装 cmake、make、gcc、g++，Debian 系可以使用以下命令:

   ```bash
   sudo apt install cmake build-essential
   ```

2. 运行 `./build` 生成可执行文件

## Install

```bash
./build install
```

- 可执行文件将被复制到 `/usr/local/bin` 目录

## 用法

```bash
judge <options>
```

- `--cmd`: 要执行的命令, 用 `@` 代替 <kbd>空格</kbd>
- `--time`: CPU 时间限制，单位：毫秒
- `--ram`: 内存限制，此项用于判断是否超限，单位：MiB
- `--output`: 输出限制，单位：MiB
- `--workdir`: 工作目录，用户程序所在目录
- `--data`: 测试数据目录，包含 `*.in`、 `*.out` 文件
- `--cpu`: 用哪个 CPU 核心

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
- 判题结果输出到 `stdout`

示例:

```json
{
  "result": 1,
  "desc": "AC",
  "total": 2,
  "passed": 2,
  "passRate": 1,
  "time": 980,
  "memory": 560, 
  "detail": [ "00-foo.out", "01-bar.out" ]
}
```

- `time`: 运行时间，单位：微秒
- `memory`: 内存占用，单位：KiB
- `total`: 测试点数量
- `passed`: 通过测试点数量
- `detail`: 通过的测试点文件名

> `time` 和 `memory` 为所有测试点中的最大值。

`result` 的取值如下:

- 1: 通过(AC)
- 2: 超时(TLE)
- 3: 内存超限(MLE)
- 4: 部分通过(PA)
- 5: 答案错误(WA)
- 7: 运行错误(RE)
- 8: 内部错误(IE)
- 9: 输出超限(OLE)

如果发生错误(IE)，输出如下：

```json
{
  "result": 8,
  "error": "错误信息..."
}
```

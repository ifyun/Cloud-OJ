# Judge Runner

OJ 判题程序，使用 chroot + setuid 创建沙盒。

## 生成可执行文件

1. 准备 Linux 环境并安装 cmake、gcc、g++，Ubuntu 可以使用以下命令

   ```bash
   sudo apt-get install cmake build-essential libboost-iostreams-dev
   ```

2. 执行 `cmake-build` 生成可执行文件

## 使用

```bash
judge-runner --options
```

- `--cmd`: 要执行的命令, 用 `@` 代替 <kbd>空格</kbd>
- `--time`: 运行时间，单位：毫秒
- `--memory`: 内存限制，此项用于判断是否超限，单位：MB
- `--max-memory`: 内存最大上限，超出此限制程序会中断并返回非零值，单位：MB
- `--output-size`: 输出限制，单位：MB
- `--workdir`: 工作目录，用户程序所在目录
- `--data`: 测试数据目录，包含 `*.in`、 `*.out` 文件
- `--proc`: 进程限制，可选参数，默认为 1

### 示例

长参数：

```bash
judge-runner --cmd=python@Solution.py \
             --time=200 \
             --memory=32 \
             --max-memory=32 \
             --output-size=8 \
             --workdir=/tmp/solution \
             --data=/tmp/test_data
```

短参数：

```bash
judge-runner -c java@-Xms16m@-Xmx512m@Solution \
             -t 200 \
             -m 64 \
             -M 512 \
             -o 8 \
             -w /tmp/solution \
             -d /tmp/test_data
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
  "status": 0,
  "result": "AC",
  "total": 2,
  "passed": 2,
  "passRate": 1,
  "time": 1,
  "memory": 3356
}
```

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

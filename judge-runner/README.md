# Judge Runner

OJ 判题程序，使用 chroot + setuid 创建沙盒。

## 生成可执行文件

1. 准备 Linux 环境并安装 cmake、gcc、g++，Ubuntu 可以使用以下命令

   ```bash
   sudo apt-get install cmake build-essential gcc-multilib g++-multilib
   ```

2. 运行 `cmake-init.sh` 生成 Makefile
3. 执行 `cmake-build` 生成可执行文件

## 使用

```bash
runner <cmd> <timeout> <memory> <max_memory> <output_size> <work_dir> <data_dir>
```

- `cmd`: 要执行的命令, 用 `@` 代替 <kbd>空格</kbd>
- `timeout`: 运行时间，单位：毫秒
- `memory`: 内存限制，此项用于判断是否超限，单位：MB
- `max_memory`: 此项为实际的内存限制，超出此限制程序会中断并返回非零值，单位：MB
- `output_size`: 输出限制，单位：MB
- `work_dir`：工作目录，用户程序所在目录
- `data_dir`: 测试数据目录，包含 `*.in`、 `*.out` 文件

### 示例

Python:

```bash
runner python@Solution.py 200 32 32 8 /tmp/userbin /test_data
```

Java:

```bash
runner java@-Xms16m@-Xmx512m@Solution 200 64 512 8 /tmp/userbin /test_data
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

- `time` 单位为毫秒
- `memory` 单位为 KB
- `total` 测试点数量
- `passed` 通过测试点数量

> `time` 和 `memory` 为所有测试点中的最大值。

`status` 的取值如下:

- 0: 通过
- 1: 答案错误
- 2: 超时
- 3: 内存超限
- 4: 输出超限
- 5: 部分通过

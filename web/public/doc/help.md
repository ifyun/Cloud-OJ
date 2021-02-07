## 支持的语言

Language        | Compiler/Interpreter  | Version
:---------------|:----------------------|:--------------------------
C               | gcc                   | std=c11
C++             | g++                   | std=c++17
Java            | OpenJDK               | 1.8
Python          | -                     | 3.5
Bash            | -                     | -
C#              | Mono                  | 4.6.2 (C# 5.0)
JavaScript      | Node                  | LTS
Kotlin          | -                     | 1.4.10
Go              | -                     | 1.15.7

## 注意事项

Java 语言的类名必须为 `Solution` 且不能使用 `package`，示例：

```java
public class Solution {
    public static void main(String[] args) {
        // Your code
    }
}
```

> 类名为其他名称将无法运行。

## 输入输出

- 你的程序必须严格按照题目要求输入输出，不能包含任何多余的内容
- 你的程序必须从标准输入流(stdin)读取输入，并将结果输出到标准输出流(stdout)

### 示例

题目：输入两个数，输出他们的和。

输入示例：

```
2 3
```

输出示例：

```
5
```

#### 示例代码

##### Python

```python
a, b = map(int, input().split())
print(a + b)
```

##### C++

```c++
#include <iostream>
using namespace std;

int main()
{
    int a, b;
    cin >> a >> b;
    cout << a + b;
    return 0;
}
```

##### Java

```java
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int a = input.nextInt();
        int b = input.nextInt();
        System.out.print(a + b);
    }
}
```

#### Go

```go
package main
import "fmt"

func main() {
    var a, b int
    fmt.Scanf("%d %d", &a, &b)
    fmt.Println(a + b)
}
```

## 判题相关

用户提交的代码，按测试点通过数量计分：得分 = (通过数量 / 总测试点数量) * 题目分值。

结果见下表：

结果        | 说明
:----------|:------------
完全正确    | -
部分通过    | -
答案错误    | -
时间超限    | -
内存超限    | -
输出超限    | 1.最大限制为 16 MB <br> 2.部分语言可能会被判断为运行错误
编译错误    | -
运行错误    | 1.存在语法错误(脚本语言) <br> 2.栈溢出、数组分配过大 <br> 3.其它原因导致判题进程中断
内部错误    | 1.判题服务器异常 <br> 2.题目没有测试数据 <br> 3.提交了恶意代码(例如 `rm -rf /*`)

### 内存与时间限制

- 对于内存和时间超限，仅超限的那一组测试点为 0 分
- 对于 Java/Kotlin/JavaScript 语言，内存限制按 2 倍计算
- 对于 Kotlin 语言，时间限制按 2 倍计算

内存最大上限一般为题目限制的 4 倍，但最大不会超过 512 MB，超出此限制为运行错误。

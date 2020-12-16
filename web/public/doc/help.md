## 帮助文档

### 支持的语言

Language        | Compiler/Interpreter/Version
----------------|-------------------------------
C               | gcc
C++             | g++ std=14
Java            | OpenJDK 1.8
Python          | 3.5
Bash            |
C#              | Mono
JavaScript      | Node v14
Kotlin          | 1.4.10

### 注意事项

Java 语言的类名必须为 `Solution`，示例：

```java
public class Solution {
    public static void main(String[] args) {
        // Your code
    }
}
```

::: info
类名为其他名称将无法运行。
:::

### 答题示例

题目：输入两个数，输出他们的和。

输入示例：

```
2 3
```

输出示例：

```
5
```

示例代码如下：

#### Python

```python
a, b = map(int, input().split())
print(a + b)
```

#### C++

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

#### Java

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

### 判题结果

用户提交的代码，按测试点通过数量计分：得分 = (通过数量 / 总测试点数量) * 此题分值。

有以下几种结果：

- 完全正确
- 部分通过
- 答案错误
- 时间超限
- 内存超限
- 编译错误
- 运行错误
- 判题异常

::: info
对于内存和时间超限，仅超限的那一组测试点为 0 分。
:::

::: info
所有题目的内存限制均为 64MB。
:::

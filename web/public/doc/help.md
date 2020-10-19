## 支持的语言

Language        | 编译器/解释器  | 备注
----------------|---------------|--------
C               | gcc           |
C++             | g++           | std=14
Java            | jdk           | 支持 Java 8 特性
Python          | python        | 仅支持 3.5
Bash Shell      |               |
C#              | Mono          |

## 注意事项

Java 语言的类名必须为 `Solution`，示例：

```java
public class Solution {
    public static void main(String[] args) {
        // Your code
    }
}
```

::: warning
类名为其他名称将无法运行
:::

## 答题示例

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

### Python

```python
a, b = map(int, input().split())
print(a + b)
```

### C++

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

### Java

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
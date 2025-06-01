<template>
  <n-config-provider abstract :hljs="highlightJs">
    <div class="wrap help">
      <n-h2>语言支持</n-h2>
      <n-table>
        <thead>
          <tr>
            <th>语言</th>
            <th>SDK</th>
            <th>选项/参数</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>C</td>
            <td>gcc 12</td>
            <td>C11</td>
          </tr>
          <tr>
            <td>C++</td>
            <td>g++ 12</td>
            <td>C++17</td>
          </tr>
          <tr>
            <td>Java</td>
            <td>OpenJDK 17</td>
            <td>Language Level 17</td>
          </tr>
          <tr>
            <td>Python</td>
            <td>Python 3.11</td>
            <td>-</td>
          </tr>
          <tr>
            <td>Bash Shell</td>
            <td>-</td>
            <td>-</td>
          </tr>
          <tr>
            <td>C#</td>
            <td>dotnet-sdk-8.0</td>
            <td>C# 12 (支持顶级语句)</td>
          </tr>
          <tr>
            <td>JavaScript</td>
            <td>Node.js 18</td>
            <td>-</td>
          </tr>
          <tr>
            <td>Kotlin</td>
            <td>Kotlin Native 2.1.x</td>
            <td>-</td>
          </tr>
          <tr>
            <td>Go</td>
            <td>go 1.24.x</td>
            <td>-</td>
          </tr>
        </tbody>
      </n-table>
      <n-h2>注意事项</n-h2>
      <n-alert :bordered="false" type="info" title="Java">
        Java 的类名必须为 Solution，且不能使用 package 关键字。
      </n-alert>
      <n-alert
        :bordered="false"
        type="info"
        title="Kotlin Native"
        style="margin-top: 24px">
        Kotlin 编译时间可能长达 30 秒以上。
      </n-alert>
      <n-alert
        :bordered="false"
        type="info"
        title="输入输出"
        style="margin-top: 24px">
        <ul>
          <li>你的程序必须严格按照题目要求输入输出，不能包含任何多余的内容</li>
          <li>
            你的程序必须从标准输入流(stdin)读取输入，并将结果输出到标准输出流(stdout)
          </li>
          <li>不存在格式错误，格式不对等同答案错误</li>
        </ul>
      </n-alert>
      <n-h2>判题相关</n-h2>
      <n-alert type="info" :bordered="false" style="margin-bottom: 24px">
        若提交成功一直获取不到结果(下表前 3
        种状态)，可以稍后查询提交记录，若一直没有结果，请联系管理员。
      </n-alert>
      <n-data-table single-column :columns="judgeColumns" :data="judgeData" />
      <n-alert
        :bordered="false"
        type="info"
        title="部分状态与错误说明"
        style="margin-top: 24px">
        <ul>
          <li>TLE/MLE 仅超限的那一组测试点为 0 分</li>
          <li>RE 之后的测试点不会运行，之前的分数保留</li>
          <li>SIGSEGV 错误：数组越界/空指针/递归栈溢出</li>
          <li>SIGKILL 错误：已知死循环会导致此错误</li>
        </ul>
      </n-alert>
      <n-h2>示例</n-h2>
      <span>A + B</span>
      <p>输入：在一行中给出两个数 A，B，以空格分隔，输出：A + B 的值。</p>
      <n-h3>C++ 示例</n-h3>
      <n-card>
        <n-code language="cpp" :code="cppSample" />
      </n-card>
      <n-h3>Python 示例</n-h3>
      <n-card>
        <n-code language="py" :code="pySample" />
      </n-card>
      <n-h3>Java 示例</n-h3>
      <n-card>
        <n-code language="java" :code="javaSample" />
      </n-card>
      <n-h3>Kotlin 示例</n-h3>
      <n-card>
        <n-code language="kotlin" :code="kotlinSample" />
      </n-card>
    </div>
  </n-config-provider>
</template>

<script lang="ts">
export default {
  name: "HelpDoc"
}
</script>

<script setup lang="ts">
import highlightJs from "highlight.js/lib/core"
import cpp from "highlight.js/lib/languages/cpp"
import java from "highlight.js/lib/languages/java"
import kotlin from "highlight.js/lib/languages/kotlin"
import py from "highlight.js/lib/languages/python"
import {
  NAlert,
  NCard,
  NCode,
  NConfigProvider,
  NDataTable,
  NH2,
  NH3,
  NTable
} from "naive-ui"

highlightJs.registerLanguage("cpp", cpp)
highlightJs.registerLanguage("py", py)
highlightJs.registerLanguage("java", java)
highlightJs.registerLanguage("kotlin", kotlin)

// region 示例代码
const cppSample = `#include <iostream>

int main()
{
    int a, b;
    std::cin >> a >> b;
    std::cout << a + b;
    return 0;
}
`

const pySample = `a, b = map(int, input().split())
print(a + b)`

const javaSample = `import java.util.*;
import java.io.*;

public class Solution {
    public static void main(String[] args) throws Exception {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
        String[] nums = bi.readLine().split(" ");
        int a = Integer.parseInt(nums[0]);
        int b = Integer.parseInt(nums[1]);
        System.out.println(a + b);
    }
}
`

const kotlinSample = `fun main() {
    var arr = readln().split(" ").map { it.toInt() }
    println(arr[0] + arr[1])
}`
// endregion

const judgeColumns = [
  {
    title: "状态",
    key: "status"
  },
  { title: "说明", key: "desc" }
]

const judgeData = [
  { status: "等待判题", desc: "提交已经加入队列" },
  { status: "正在编译", desc: "Kotlin 用户会经常看到这个状态" },
  { status: "正在运行", desc: "字面意思，但你可能很少看到这个状态" },
  { status: "部分通过 PA", desc: "可能有一些情况没有考虑到" },
  { status: "完全正确 AC", desc: "字面意思" },
  { status: "答案错误 WA", desc: "再改亿遍！" },
  { status: "时间超限 TLE", desc: "时间超限不能说明答案是正确的！" },
  { status: "内存超限 MLE", desc: "内存超限也不能说明答案是正确的！" },
  { status: "输出超限 OLE", desc: "程序产生的输出超过题目限制" },
  { status: "编译错误 CE", desc: "本地能运行？那可能版本不对" },
  {
    status: "运行错误 RE",
    desc: "语法错误(解释型语言)或其它原因导致判题程序停止"
  },
  { status: "内部错误 IE", desc: "判题服务器出问题了，快滴滴管理员" }
]
</script>

<style scoped lang="scss">
.help {
  display: flex;
  flex-direction: column;

  ul {
    margin: 0;
    padding-left: 18px;
  }
}
</style>

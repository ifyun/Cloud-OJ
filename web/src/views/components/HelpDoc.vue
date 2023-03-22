<template>
  <n-config-provider :hljs="highlightJs">
    <div class="help">
      <n-h1 style="margin-bottom: 0">帮助文档</n-h1>
      <n-h2>语言支持</n-h2>
      <n-data-table :columns="languageColumns" :data="languageData" />
      <n-h2>注意事项</n-h2>
      <n-alert :bordered="false" type="warning" title="Java 特殊要求">
        Java 的类名必须为 Solution，且不能使用 package 关键字！
      </n-alert>
      <n-alert
        :bordered="false"
        type="warning"
        title="输入输出"
        style="margin-top: 25px">
        <ul>
          <li>你的程序必须严格按照题目要求输入输出，不能包含任何多余的内容</li>
          <li>
            你的程序必须从标准输入流(stdin)读取输入，并将结果输出到标准输出流(stdout)
          </li>
          <li>请确保：在所有内容全部输出后，末尾至少包含一个换行符</li>
        </ul>
      </n-alert>
      <n-h2>判题相关</n-h2>
      <n-data-table
        :single-line="false"
        :columns="judgeColumns"
        :data="judgeData" />
      <n-alert
        :bordered="false"
        type="info"
        title="内存与时间限制"
        style="margin-top: 25px">
        <ul>
          <li>对于内存和时间超限，仅超限的那一组测试点为 0 分</li>
          <li>对于 Java/Kotlin/JavaScript 语言，内存限制按 2 倍计算</li>
          <li>对于 Kotlin 语言，时间限制按 2 倍计算</li>
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
    </div>
  </n-config-provider>
</template>

<script setup lang="ts">
import { onBeforeMount } from "vue"
import {
  NAlert,
  NCard,
  NCode,
  NConfigProvider,
  NDataTable,
  NH1,
  NH2,
  NH3
} from "naive-ui"
import highlightJs from "highlight.js/lib/core"
import cpp from "highlight.js/lib/languages/cpp"
import py from "highlight.js/lib/languages/python"
import java from "highlight.js/lib/languages/java"
import { setTitle } from "@/utils"

highlightJs.registerLanguage("cpp", cpp)
highlightJs.registerLanguage("py", py)
highlightJs.registerLanguage("java", java)

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

public class Solution {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int a = input.nextInt();
        int b = input.nextInt();
        System.out.print(a + b);
    }
}
`
// endregion

const languageColumns = [
  { title: "语言", key: "lang" },
  { title: "编译器/解释器", key: "compiler" },
  { title: "版本/参数", key: "version" }
]

const languageData = [
  { lang: "C", compiler: "gcc", version: "std=c11" },
  { lang: "C++", compiler: "g++", version: "std=c++17" },
  { lang: "Java", compiler: "OpenJDK", version: "17(Level 1.8)" },
  { lang: "Python", compiler: "python3", version: "3.x" },
  { lang: "Bash Shell", compiler: "-", version: "-" },
  { lang: "C#", compiler: "Mono", version: "C# 5.0" },
  { lang: "JavaScript", compiler: "Node.js", version: "LTS" },
  { lang: "Kotlin", compiler: "-", version: "1.7.x" },
  { lang: "Go", compiler: "-", version: "1.19" }
]

const judgeColumns = [
  {
    title: "状态",
    key: "status",
    rowSpan: (rowData: any, index: number) => {
      if (index === 8 || index === 10) {
        return 2
      } else {
        return 1
      }
    }
  },
  { title: "说明", key: "desc" }
]

const judgeData = [
  { status: "等待判题", desc: "你的提交已经加入队列，不要着急" },
  { status: "部分通过", desc: "你可能有一些特殊情况没有考虑到" },
  { status: "完全正确 AC", desc: "恭喜你通过了所有测试点" },
  { status: "答案错误 WA", desc: "再接再厉！" },
  { status: "时间超限 TLE", desc: "注意！时间超限不能说明答案是正确的" },
  { status: "内存超限 MLE", desc: "注意！内存超限也不能说明答案是正确的" },
  { status: "输出超限 OLE", desc: "你的算法可能不太对" },
  { status: "编译错误 CE", desc: "如果本地能运行那可能版本不对" },
  { status: "运行错误 RE", desc: "1.如果是脚本语言可能存在语法错误" },
  { desc: "2.其它原因导致判题程序中断，具体可以参考错误信息" },
  { status: "内部错误 IE", desc: "1.题目没有测试数据" },
  { desc: "2.判题服务器出问题了" }
]

onBeforeMount(() => {
  setTitle("帮助文档")
})
</script>

<style scoped lang="scss">
.help {
  display: flex;
  flex-direction: column;
  width: 1100px;
  padding: var(--layout-padding) 0;
  margin: 0 auto;

  ul {
    margin: 0;
    padding-left: 18px;
  }
}
</style>

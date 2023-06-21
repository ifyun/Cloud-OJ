<template>
  <n-config-provider :hljs="highlightJs">
    <div class="help">
      <n-h2>语言支持</n-h2>
      <n-data-table
        single-column
        :columns="languageColumns"
        :data="languageData" />
      <n-h2>注意事项</n-h2>
      <n-alert :bordered="false" type="warning" title="Java">
        Java 的类名必须为 Solution，且不能使用 package 关键字。
      </n-alert>
      <n-alert
        :bordered="false"
        type="warning"
        title="Kotlin Native"
        style="margin-top: 24px">
        Kotlin 编译时间可能长达 30 秒以上。
      </n-alert>
      <n-alert
        :bordered="false"
        type="warning"
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
        如果提交成功却获取不到结果，说明还没有入库
      </n-alert>
      <n-data-table single-column :columns="judgeColumns" :data="judgeData" />
      <n-alert :bordered="false" type="info" style="margin-top: 24px">
        对于内存和时间超限，仅超限的那一组测试点为 0 分
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

<script setup lang="ts">
import { onBeforeMount } from "vue"
import {
  NAlert,
  NCard,
  NCode,
  NConfigProvider,
  NDataTable,
  NH2,
  NH3
} from "naive-ui"
import highlightJs from "highlight.js/lib/core"
import cpp from "highlight.js/lib/languages/cpp"
import py from "highlight.js/lib/languages/python"
import java from "highlight.js/lib/languages/java"
import kotlin from "highlight.js/lib/languages/kotlin"
import { setTitle } from "@/utils"

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

const languageColumns = [
  { title: "语言", key: "lang" },
  { title: "编译器/解释器", key: "compiler" },
  { title: "版本/参数", key: "version" }
]

const languageData = [
  { lang: "C", compiler: "gcc", version: "11" },
  { lang: "C++", compiler: "g++", version: "17" },
  { lang: "Java", compiler: "OpenJDK", version: "Language Level 1.8" },
  { lang: "Python", compiler: "Python3", version: "3.x" },
  { lang: "Bash Shell", compiler: "-", version: "-" },
  { lang: "C#", compiler: "Mono", version: "C# 6.0" },
  { lang: "JavaScript", compiler: "Node.js", version: "18.x" },
  { lang: "Kotlin", compiler: "kotlinc-native", version: "1.8.x" },
  { lang: "Go", compiler: "-", version: "1.20.x" }
]

const judgeColumns = [
  {
    title: "状态",
    key: "status"
  },
  { title: "说明", key: "desc" }
]

const judgeData = [
  { status: "等待判题", desc: "你的提交已经加入队列，等等就好" },
  { status: "部分通过", desc: "你可能有一些特殊情况没有考虑到" },
  { status: "完全正确 AC", desc: "恭喜你通过了所有测试点" },
  { status: "答案错误 WA", desc: "再接再厉！" },
  { status: "时间超限 TLE", desc: "注意！时间超限不能说明答案是正确的" },
  { status: "内存超限 MLE", desc: "注意！内存超限也不能说明答案是正确的" },
  { status: "输出超限 OLE", desc: "程序产生的输出超过题目限制" },
  { status: "编译错误 CE", desc: "如果本地能运行那可能版本不对" },
  {
    status: "运行错误 RE",
    desc: "如果是脚本语言可能存在语法错误 / 其它原因导致判题程序中断，具体可以参考错误信息"
  },
  { status: "内部错误 IE", desc: "题目没有测试数据 / 判题服务器出问题了" }
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

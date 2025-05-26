<template>
  <n-config-provider abstract :hljs="highlightJs">
    <n-page-header @back="back">
      <template #title>
        {{ `${data.problemId}.${data.title}` }}
      </template>
      <template #extra>
        <router-link
          :to="{ name: 'submission', params: { pid: data.problemId } }">
          <n-button quaternary type="info">进入题目页面</n-button>
        </router-link>
      </template>
      <n-flex vertical>
        <n-table
          v-if="showUser"
          size="small"
          style="text-align: center"
          :single-line="false">
          <thead>
            <tr>
              <th>Solution ID</th>
              <th>UID</th>
              <th>用户名</th>
              <th>昵称</th>
              <th>真实姓名</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ data.solutionId }}</td>
              <td>{{ data.uid }}</td>
              <td>{{ data.username }}</td>
              <td>{{ data.nickname }}</td>
              <td>
                <n-text strong type="error">{{ data.realName }}</n-text>
              </td>
            </tr>
          </tbody>
        </n-table>
        <n-table size="small" style="text-align: center" :single-line="false">
          <thead>
            <tr>
              <th>判题结果</th>
              <th>编程语言</th>
              <th>CPU 时间</th>
              <th>内存占用</th>
              <th>得分</th>
              <th>通过测试点</th>
              <th>提交时间</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>
                <n-text strong :type="ResultTypes[data.result!]">
                  {{ data.resultText }}
                </n-text>
              </td>
              <td>
                <n-text
                  strong
                  :style="{ color: LanguageColors[data.language!] }">
                  {{ LanguageNames[data.language!] }}
                </n-text>
              </td>
              <td>{{ timeUsage(data.time) }}</td>
              <td>{{ ramUsage(data.memory) }}</td>
              <td>
                <n-text strong>{{ data.score ?? "-" }}</n-text>
              </td>
              <td>{{ data.total ? `${data.passed} / ${data.total}` : "-" }}</td>
              <td>{{ time(data.submitTime!) }}</td>
            </tr>
          </tbody>
        </n-table>
        <n-card>
          <n-code
            trim
            show-line-numbers
            :code="data.sourceCode"
            :language="langs[data.language!]" />
        </n-card>
      </n-flex>
      <template v-if="data.errorInfo" #footer>
        <n-divider
          dashed
          title-placement="left"
          style="margin-top: 0; margin-bottom: 14px">
          错误信息
        </n-divider>
        <n-card>
          <n-text type="error">
            <pre id="error-info">{{ data.errorInfo }}</pre>
          </n-text>
        </n-card>
      </template>
    </n-page-header>
  </n-config-provider>
</template>

<script setup lang="ts">
import { JudgeResult } from "@/api/type"
import { LanguageColors, LanguageNames, ResultTypes } from "@/type"
import { ramUsage, timeUsage } from "@/utils"
import dayjs from "dayjs"
import highlightJs from "highlight.js/lib/core"
import bash from "highlight.js/lib/languages/bash"
import c from "highlight.js/lib/languages/c"
import cpp from "highlight.js/lib/languages/cpp"
import cs from "highlight.js/lib/languages/csharp"
import go from "highlight.js/lib/languages/go"
import java from "highlight.js/lib/languages/java"
import js from "highlight.js/lib/languages/javascript"
import kt from "highlight.js/lib/languages/kotlin"
import py from "highlight.js/lib/languages/python"
import {
  NButton,
  NCard,
  NCode,
  NConfigProvider,
  NDivider,
  NFlex,
  NPageHeader,
  NTable,
  NText
} from "naive-ui"

const langs = ["c", "cpp", "java", "py", "bash", "cs", "js", "kt", "go"]
const langFns = [c, cpp, java, py, bash, cs, js, kt, go]

langs.forEach((v, i) => {
  highlightJs.registerLanguage(v, langFns[i])
})

defineProps<{ show: boolean; showUser: boolean; data: JudgeResult }>()
const emit = defineEmits<{
  (e: "update:show", value: boolean): void
}>()

function back() {
  emit("update:show", false)
}

function time(time: number) {
  const now = dayjs()
  const t = dayjs(time)

  if (t.isSame(now, "day")) {
    return t.format("今天 H:mm:ss.SSS")
  } else if (t.isSame(now, "year")) {
    return t.format("M 月 D 日 H:mm:ss.SSS")
  } else {
    return t.format("YYYY 年 M 月 D 日 H:mm:ss.SSS")
  }
}
</script>

<style scoped>
#error-info {
  margin: 0;
  font-family: inherit;
  white-space: pre-wrap;
}
</style>
